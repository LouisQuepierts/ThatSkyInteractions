package net.quepierts.thatskyinteractions.common.data.global;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.common.data.Codecs;
import net.quepierts.thatskyinteractions.common.data.astrolabe.LitRequest;
import net.quepierts.thatskyinteractions.common.data.attachment.UserDataAttachment;
import net.quepierts.thatskyinteractions.common.data.attachment.component.AstrolabeComponent;
import net.quepierts.thatskyinteractions.common.data.manager.InteractTreeManager;
import net.quepierts.thatskyinteractions.common.data.tree.InteractTree;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class TSIGlobalData extends SavedData {
    public record SerializableData(
            Object2ObjectMap<UUID, Set<LitRequest>> litRequests    /* UUID: receiver */
    ) {
        public static final Codec<SerializableData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codecs.<UUID, Set<LitRequest>, Object2ObjectMap<UUID, Set<LitRequest>>>map(
                        UUIDUtil.CODEC, Codecs.set(LitRequest.CODEC), Object2ObjectOpenHashMap::new
                ).fieldOf("litRequests").forGetter(SerializableData::litRequests)
        ).apply(instance, SerializableData::new));

        public static SerializableData empty() {
            return new SerializableData(new Object2ObjectOpenHashMap<>());
        }
    }

    public static final String ID = "thatskyinteractions";
    public static final SavedData.Factory<TSIGlobalData> FACTORY = new SavedData.Factory<>(
            TSIGlobalData::new,
            TSIGlobalData::load
    );

    public static final ResourceLocation FRIEND_INTERACT_TREE = ThatSkyInteractions.getLocation("friend");

    public static TSIGlobalData getGlobalRelationData(ServerLevel level) {
        return level.getDataStorage().get(FACTORY, ID);
    }

    public static TSIGlobalData getGlobalRelationData(MinecraftServer server) {
        return Objects.requireNonNull(server.getLevel(Level.OVERWORLD)).getDataStorage().get(FACTORY, ID);
    }

    @NotNull
    private SerializableData data;
    private final InteractTree tree;

    public static void init(ServerLevel level) {
        level.getDataStorage().computeIfAbsent(FACTORY, ID);
    }

    private TSIGlobalData() {
        this.setDirty(true);
        data = SerializableData.empty();

        this.tree = InteractTreeManager.INSTANCE.get(FRIEND_INTERACT_TREE);
    }

    private static TSIGlobalData load(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        ThatSkyInteractions.LOGGER.info("Load TSI Data");
        TSIGlobalData data = new TSIGlobalData();
        data.loadInner(tag);
        return data;
    }

    @Override @NotNull
    public CompoundTag save(@NotNull CompoundTag compoundTag, @NotNull HolderLookup.Provider provider) {
        SerializableData.CODEC.encode(this.data, NbtOps.INSTANCE, compoundTag);
        return compoundTag;
    }

    public void update(long day) {
        if (this.data.litRequests().isEmpty()) {
            return;
        }

        boolean modified = false;
        Object2ObjectMap<UUID, Set<LitRequest>> litRequests = this.data.litRequests;
        ObjectIterator<Map.Entry<UUID, Set<LitRequest>>> iterator = litRequests.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<UUID, Set<LitRequest>> entry = iterator.next();
            entry.getValue().removeIf(request -> request.requestDay() < day);

            if (entry.getValue().isEmpty()) {
                iterator.remove();
            }

            modified = true;
        }

        if (modified) {
            this.setDirty();
        }
    }

    public void tryAward(@NotNull ServerPlayer player) {
        UUID uuid = player.getUUID();

        Object2ObjectMap<UUID, Set<LitRequest>> litRequests = this.data.litRequests();
        if (litRequests.containsKey(uuid)) {
            AstrolabeComponent astrolabe = UserDataAttachment.getAttachment(player).getAstrolabe();

            Set<LitRequest> requests = litRequests.get(uuid);
            for (LitRequest request : requests) {
                astrolabe.awardLight(request.sender());
            }
            litRequests.remove(uuid);

            this.setDirty();
        }
    }

    public boolean lit(@NotNull ServerPlayer sender, @NotNull UUID target, @NotNull MinecraftServer server) {
        ServerLevel level = server.getLevel(Level.OVERWORLD);
        PlayerList playerList = server.getPlayerList();
        ServerPlayer targetPlayer = playerList.getPlayer(target);

        UserDataAttachment.getAttachment(sender).getAstrolabe().sendLight(target);

        if (targetPlayer == null) {
            Set<LitRequest> requests = this.data.litRequests().computeIfAbsent(target, u -> new ObjectLinkedOpenHashSet<>());
            requests.add(new LitRequest(sender.getUUID(), level.getGameTime() / 24000L));
            this.setDirty();
            return false;
        }

        UserDataAttachment.getAttachment(targetPlayer).getAstrolabe().awardLight(sender.getUUID());
        return true;
    }

    private void loadInner(@NotNull CompoundTag tag) {
        this.setDirty(false);
        if (tree == null) {
            ThatSkyInteractions.LOGGER.warn("Data Error");
            return;
        }

        DataResult<Pair<SerializableData, Tag>> decode = SerializableData.CODEC.decode(NbtOps.INSTANCE, tag);

        decode.ifSuccess(pair -> this.data = pair.getFirst());
    }
}
