package net.quepierts.thatskyinteractions.data;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.block.entity.WingOfLightBlockEntity;
import net.quepierts.thatskyinteractions.data.astrolabe.AstrolabeManager;
import net.quepierts.thatskyinteractions.data.astrolabe.AstrolabeMap;
import net.quepierts.thatskyinteractions.data.astrolabe.FriendAstrolabeInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("unused")
public class TSIUserData {
    @NotNull private final AstrolabeMap astrolabes;
    @NotNull private final Set<UUID> blackList;
    @NotNull private final Map<UUID, Pair<FriendAstrolabeInstance.NodeData, ResourceLocation>> cache;
    @NotNull private final Set<UUID> pickedWingOfLight;
    private long savedTime;

    public TSIUserData(@NotNull AstrolabeMap astrolabes, @NotNull Set<UUID> blackList, @NotNull Set<UUID> pickedWingOfLight, long savedTime) {
        this.astrolabes = astrolabes;
        this.blackList = blackList;
        this.pickedWingOfLight = pickedWingOfLight;
        this.savedTime = savedTime;

        this.cache = new Object2ObjectOpenHashMap<>();
        for (Map.Entry<ResourceLocation, FriendAstrolabeInstance> entry : this.astrolabes.entrySet()) {
            for (FriendAstrolabeInstance.NodeData node : entry.getValue().getNodes()) {
                if (node == null)
                    continue;
                this.cache.put(node.getFriendData().getUuid(), Pair.of(node, entry.getKey()));
            }

        }
    }

    public static TSIUserData create() {
        AstrolabeManager manager = ThatSkyInteractions.getInstance().getProxy().getAstrolabeManager();
        ResourceLocation first = manager.getBestFriendAstrolabes().getFirst();
        ResourceLocation generated = manager.getGeneratedLocation(0);
        AstrolabeMap astrolabeMap = new AstrolabeMap();
        astrolabeMap.put(first, new FriendAstrolabeInstance(first));
        astrolabeMap.put(generated, new FriendAstrolabeInstance(generated));

        return new TSIUserData(astrolabeMap, new HashSet<>(), new HashSet<>(), System.currentTimeMillis());
    }
    public static void toNetwork(FriendlyByteBuf byteBuf, TSIUserData data) {
        AstrolabeMap.toNetwork(byteBuf, data.astrolabes);
        byteBuf.writeCollection(data.blackList, (o, uuid) -> o.writeUUID(uuid));
        byteBuf.writeCollection(data.pickedWingOfLight, (o, uuid) -> o.writeUUID(uuid));
        byteBuf.writeLong(data.savedTime);
    }

    public static TSIUserData fromNetwork(FriendlyByteBuf byteBuf) {
        AstrolabeMap astrolabes = AstrolabeMap.fromNetwork(byteBuf);
        Set<UUID> blackList = byteBuf.readCollection(ObjectOpenHashSet::new, (o) -> o.readUUID());
        Set<UUID> pickedWingOfLight = byteBuf.readCollection(ObjectOpenHashSet::new, (o) -> o.readUUID());
        long l = byteBuf.readLong();
        return new TSIUserData(astrolabes, blackList, pickedWingOfLight, l);
    }

    public static void toNBT(CompoundTag tag, TSIUserData data) {
        data.savedTime = System.currentTimeMillis();
        tag.put("astrolabe", AstrolabeMap.toNBT(new CompoundTag(), data.astrolabes));
        ListTag blackList = new ListTag();
        for (UUID uuid : data.blackList) {
            blackList.add(NbtUtils.createUUID(uuid));
        }
        tag.put("blackList", blackList);
        ListTag pickedWingOfLight = new ListTag();
        for (UUID uuid : data.pickedWingOfLight) {
            pickedWingOfLight.add(NbtUtils.createUUID(uuid));
        }
        tag.put("pickedWingOfLight", pickedWingOfLight);
        tag.putLong("lastUpdateTime", data.savedTime);
    }

    public static TSIUserData fromNBT(CompoundTag tag) {
        AstrolabeMap astrolabes = AstrolabeMap.fromNBT(tag.getCompound("astrolabe"));
        ListTag blackListTag = tag.getList("blackList", ListTag.TAG_INT_ARRAY);
        ObjectOpenHashSet<UUID> blackList = new ObjectOpenHashSet<>(blackListTag.size());
        for (Tag uuid : blackListTag) {
            blackList.add(NbtUtils.loadUUID(uuid));
        }
        ListTag pickedWingOfLightTag = tag.getList("pickedWingOfLight", Tag.TAG_INT_ARRAY);
        ObjectOpenHashSet<UUID> pickedWingOfLight = new ObjectOpenHashSet<>(pickedWingOfLightTag.size());
        for (Tag uuid : pickedWingOfLightTag) {
            pickedWingOfLight.add(NbtUtils.loadUUID(uuid));
        }
        long savedTime = tag.getLong("lastUpdateTime");
        return new TSIUserData(astrolabes, blackList, pickedWingOfLight, savedTime);
    }

    @Nullable
    @SuppressWarnings("all")
    public Pair<FriendAstrolabeInstance.NodeData, ResourceLocation> addFriend(Player player) {
        if (player == null)
            return null;

        if (this.isFriend(player.getUUID()))
            return null;

        @Nullable Pair<FriendAstrolabeInstance.NodeData, ResourceLocation> data = this.astrolabes.addFriend(player);
        if (data != null) {
            this.cache.put(player.getUUID(), data);
        }
        return data;
    }

    /* debug */
    public @Nullable Pair<FriendAstrolabeInstance.NodeData, ResourceLocation> addFriend(UUID player) {
        if (player == null)
            return null;

        if (this.isFriend(player))
            return null;

        @Nullable Pair<FriendAstrolabeInstance.NodeData, ResourceLocation> data = this.astrolabes.addFriend(player);
        if (data != null) {
            this.cache.put(player, data);
        }
        return data;
    }

    public boolean isPickedUp(@NotNull WingOfLightBlockEntity wingOfLightBlockEntity) {
        UUID uuid = wingOfLightBlockEntity.getUUID();
        return this.pickedWingOfLight.contains(uuid);
    }

    public boolean isPickedUp(UUID wolUUID) {
        return this.pickedWingOfLight.contains(wolUUID);
    }

    public void pickupWingOfLight(@NotNull UUID wolUUID) {
        this.pickedWingOfLight.add(wolUUID);
    }

    public boolean likeFriend(UUID player) {
        Pair<FriendAstrolabeInstance.NodeData, ResourceLocation> cache = this.cache.get(player);
        if (cache == null)
            return false;

        ResourceLocation astrolabe = this.astrolabes.getOrCreateBestFriendAstrolabe();

        if (astrolabe == null)
            return false;

        this.transfer(cache.getFirst(), cache.getSecond(), astrolabe);
        return true;
    }

    public boolean unlikeFriend(UUID player) {
        Pair<FriendAstrolabeInstance.NodeData, ResourceLocation> cache = this.cache.get(player);
        if (cache == null)
            return false;

        if (cache.getSecond().getPath().startsWith(AstrolabeManager.GENERATED_PREFIX))
            return false;

        ResourceLocation astrolabe = this.astrolabes.getOrCreateFriendAstrolabe();

        if (astrolabe == null)
            return false;

        this.transfer(cache.getFirst(), cache.getSecond(), astrolabe);
        return true;
    }

    private void transfer(@NotNull FriendAstrolabeInstance.NodeData data, @NotNull ResourceLocation srcLocation, @NotNull ResourceLocation destLocation) {
        FriendAstrolabeInstance src = this.astrolabes.get(srcLocation);
        FriendAstrolabeInstance dest = this.astrolabes.get(destLocation);
        src.peek(data);
        dest.put(data);

        this.cache.put(data.getFriendData().getUuid(), Pair.of(data, destLocation));
    }

    public void move(UUID friendUUID, ResourceLocation destLocation, int destIndex) {
        Pair<FriendAstrolabeInstance.NodeData, ResourceLocation> cache = this.cache.get(friendUUID);
        if (cache == null)
            return;
        ResourceLocation srcLocation = cache.getSecond();
        FriendAstrolabeInstance src = this.astrolabes.get(srcLocation);
        int srcIndex = src.indexOf(cache.getFirst());
        if (this.astrolabes.move(srcLocation, destLocation, srcIndex, destIndex)) {
            this.cache.put(friendUUID, Pair.of(cache.getFirst(), destLocation));
        }
    }

    public void createAstrolabe(ResourceLocation location) {
        this.astrolabes.getOrCreate(location);
    }

    public void setOnline(UUID player, boolean online) {
        FriendAstrolabeInstance.NodeData data = this.cache.get(player).getFirst();
        if (data != null) {
            data.setFlag(FriendAstrolabeInstance.Flag.ONLINE, online);
        }
    }

    public boolean sendLight(UUID player) {
        Pair<FriendAstrolabeInstance.NodeData, ResourceLocation> pair = this.cache.get(player);

        if (pair == null)
            return false;

        FriendAstrolabeInstance.NodeData data = pair.getFirst();

        if (data == null)
            return false;

        if (data.hasFlag(FriendAstrolabeInstance.Flag.SENT))
            return false;
        data.setFlag(FriendAstrolabeInstance.Flag.SENT, true);
        return true;
    }

    public void awardLight(UUID player) {
        Pair<FriendAstrolabeInstance.NodeData, ResourceLocation> pair = this.cache.get(player);

        if (pair == null)
            return;

        FriendAstrolabeInstance.NodeData data = pair.getFirst();
        if (data != null) {
            data.setFlag(FriendAstrolabeInstance.Flag.RECEIVED, true);
        }
    }

    public boolean gainLight(UUID player) {
        Pair<FriendAstrolabeInstance.NodeData, ResourceLocation> pair = this.cache.get(player);

        if (pair == null)
            return false;

        FriendAstrolabeInstance.NodeData first = pair.getFirst();

        if (!first.hasFlag(FriendAstrolabeInstance.Flag.RECEIVED))
            return false;
        first.setFlag(FriendAstrolabeInstance.Flag.RECEIVED, false);
        return true;
    }

    public Set<UUID> blackList() {
        return blackList;
    }

    public AstrolabeMap astrolabes() {
        return astrolabes;
    }

    public @NotNull Map<UUID, Pair<FriendAstrolabeInstance.NodeData, ResourceLocation>> cache() {
        return cache;
    }

    public boolean isBlocked(UUID player) {
        return this.blackList.contains(player);
    }

    public void block(UUID player) {
        this.blackList.add(player);
    }

    public void unblock(UUID player) {
        this.blackList.remove(player);
    }

    public boolean isFriend(UUID player) {
        return this.cache.containsKey(player);
    }

    @Nullable
    public FriendAstrolabeInstance.NodeData getNodeData(UUID target) {
        Pair<FriendAstrolabeInstance.NodeData, ResourceLocation> cache = this.cache.get(target);
        if (cache == null)
            return null;
        return cache.getFirst();
    }

    public boolean isLiked(UUID player) {
        Pair<FriendAstrolabeInstance.NodeData, ResourceLocation> pair = this.cache.get(player);
        return pair != null && !pair.getSecond().getPath().startsWith(AstrolabeManager.GENERATED_PREFIX);
    }
}
