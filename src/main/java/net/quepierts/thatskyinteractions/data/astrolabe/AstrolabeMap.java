package net.quepierts.thatskyinteractions.data.astrolabe;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class AstrolabeMap extends Object2ObjectOpenHashMap<ResourceLocation, FriendAstrolabeInstance> {
    public AstrolabeMap() {
        super(5);
    }

    protected AstrolabeMap(int size) {
        super(size);
    }

    public boolean move(@NotNull ResourceLocation srcAstrolabe, @NotNull ResourceLocation destAstrolabe, int srcIndex, int destIndex) {
        boolean isSameAstrolabe = srcAstrolabe.equals(destAstrolabe);
        if (isSameAstrolabe && srcIndex == destIndex)
            return false;

        if (srcIndex > 10 || srcIndex < 0 || destIndex > 10 || destIndex < 0)
            return false;

        FriendAstrolabeInstance srcInstance = this.get(srcAstrolabe);

        if (isSameAstrolabe) {
            srcInstance.swap(srcIndex, destIndex);
            return true;
        }

        FriendAstrolabeInstance destInstance = this.get(destAstrolabe);

        if (srcInstance == null || destInstance == null)
            return false;

        FriendAstrolabeInstance.NodeData peeked = srcInstance.peek(srcIndex);
        destInstance.put(destIndex, peeked);
        return true;
    }

    @Nullable
    public Pair<FriendAstrolabeInstance.NodeData, ResourceLocation> addFriend(UUID uuid) {
        ImmutableList<ResourceLocation> friendAstrolabes = AstrolabeManager.GENERATED_ASTROLABES;
        for (ResourceLocation astrolabe : friendAstrolabes) {
            FriendAstrolabeInstance instance = this.getOrCreate(astrolabe);
            if (instance != null && !instance.isFulled()) {
                return Pair.of(instance.addFriend(uuid), astrolabe);
            }
        }
        return null;
    }

    @Nullable
    public Pair<FriendAstrolabeInstance.NodeData, ResourceLocation> addFriend(Player player) {
        ImmutableList<ResourceLocation> friendAstrolabes = AstrolabeManager.GENERATED_ASTROLABES;
        for (ResourceLocation astrolabe : friendAstrolabes) {
            FriendAstrolabeInstance instance = this.getOrCreate(astrolabe);

            if (instance != null) {
                FriendAstrolabeInstance.NodeData data = instance.addFriend(player);
                if (data == null)
                    continue;
                return Pair.of(data, astrolabe);
            }
        }
        return null;
    }

    public static void toNetwork(FriendlyByteBuf byteBuf, AstrolabeMap map) {
        byteBuf.writeMap(
                map,
                FriendlyByteBuf::writeResourceLocation,
                FriendAstrolabeInstance::toNetwork
        );
    }

    public static AstrolabeMap fromNetwork(FriendlyByteBuf byteBuf) {
        return byteBuf.readMap(
                AstrolabeMap::new,
                FriendlyByteBuf::readResourceLocation,
                FriendAstrolabeInstance::fromNetwork
        );
    }

    public static CompoundTag toNBT(CompoundTag tag, AstrolabeMap map) {
        for (Map.Entry<ResourceLocation, FriendAstrolabeInstance> entry : map.entrySet()) {
            CompoundTag astData = new CompoundTag();
            FriendAstrolabeInstance.toNBT(astData, entry.getValue());
            tag.put(entry.getKey().toString(), astData);
        }
        return tag;
    }

    public static AstrolabeMap fromNBT(CompoundTag tag) {
        final Set<String> allKeys = tag.getAllKeys();
        final AstrolabeMap map = new AstrolabeMap(allKeys.size());
        for (String key : allKeys) {
            ResourceLocation location = ResourceLocation.parse(key);
            FriendAstrolabeInstance nbt = FriendAstrolabeInstance.fromNBT(tag.getCompound(key));
            map.put(location, nbt);
        }
        return map;
    }

    public ResourceLocation getOrCreateBestFriendAstrolabe() {
        ObjectList<ResourceLocation> bestFriendAstrolabes = ThatSkyInteractions.getInstance().getProxy().getAstrolabeManager().getBestFriendAstrolabes();
        return this.innerGet(bestFriendAstrolabes);
    }

    public ResourceLocation getOrCreateFriendAstrolabe() {
        ImmutableList<ResourceLocation> friendAstrolabes = AstrolabeManager.GENERATED_ASTROLABES;
        return this.innerGet(friendAstrolabes);
    }

    private ResourceLocation innerGet(List<ResourceLocation> astrolabes) {
        for (ResourceLocation location : astrolabes) {
            final FriendAstrolabeInstance instance = this.get(location);

            if (instance == null) {
                this.put(location, new FriendAstrolabeInstance(location));
                return location;
            }

            if (!instance.isFulled())
                return location;
        }

        return null;
    }

    public FriendAstrolabeInstance getOrCreate(ResourceLocation location) {
        if (this.containsKey(location))
            return this.get(location);

        try {
            return this.put(location, new FriendAstrolabeInstance(location));
        } catch (Exception e) {
            ThatSkyInteractions.LOGGER.warn("Cannot create astrolabe {}", location, e);
            return null;
        }
    }

    public void update() {
        for (FriendAstrolabeInstance instance : this.values()) {
            instance.update();
        }
    }
}
