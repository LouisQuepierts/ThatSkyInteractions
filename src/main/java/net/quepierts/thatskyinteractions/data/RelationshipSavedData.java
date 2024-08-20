package net.quepierts.thatskyinteractions.data;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.quepierts.simpleanimator.core.SimpleAnimator;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.data.tree.InteractTree;
import net.quepierts.thatskyinteractions.data.tree.InteractTreeInstance;
import net.quepierts.thatskyinteractions.network.packet.BatchRelationshipPacket;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class RelationshipSavedData extends SavedData {
    public static final String ID = "relationship";
    public static final SavedData.Factory<RelationshipSavedData> FACTORY = new SavedData.Factory<>(
            RelationshipSavedData::new,
            RelationshipSavedData::load
    );
    public static final ResourceLocation FRIEND_INTERACT_TREE = ThatSkyInteractions.getLocation("friend");
    private static final String KEY_RELATIONSHIP = "relationship";

    public static RelationshipSavedData get(ServerLevel level) {
        return level.getDataStorage().get(FACTORY, ID);
    }

    private final Object2ObjectMap<PlayerPair, InteractTreeInstance> relationship;
    private final Object2ObjectMap<UUID, ObjectOpenHashSet<PlayerPair>> reference;
    private final InteractTree tree;
    public static void init(ServerLevel level) {
        level.getDataStorage().computeIfAbsent(FACTORY, ID);
    }

    private RelationshipSavedData() {
        this.setDirty(true);
        relationship = new Object2ObjectOpenHashMap<>();
        reference = new Object2ObjectOpenHashMap<>();

        this.tree = ThatSkyInteractions.getInstance().getProxy().getInteractTreeManager().get(FRIEND_INTERACT_TREE);
    }

    private static RelationshipSavedData load(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        ThatSkyInteractions.LOGGER.info("Load TSI Data");
        RelationshipSavedData data = new RelationshipSavedData();
        data.loadInner(tag, provider);
        return data;
    }

    @Override @NotNull
    public CompoundTag save(@NotNull CompoundTag compoundTag, @NotNull HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag relationship = new ListTag();
        for (Map.Entry<PlayerPair, InteractTreeInstance> entry : this.relationship.entrySet()) {
            CompoundTag pair = new CompoundTag();
            entry.getKey().serializeNBT(pair);
            entry.getValue().serializeNBT(pair);
            relationship.add(pair);
        }
        tag.put(KEY_RELATIONSHIP, relationship);
        return tag;
    }

    public void sync(OnDatapackSyncEvent event) {
        ServerPlayer player = event.getPlayer();

        if (player != null) {
            SimpleAnimator.getNetwork().sendToPlayer(new BatchRelationshipPacket(getPlayerMap(player.getUUID())), player);
        } else {
            for (ServerPlayer serverPlayer : event.getPlayerList().getPlayers()) {
                SimpleAnimator.getNetwork().sendToPlayer(new BatchRelationshipPacket(getPlayerMap(serverPlayer.getUUID())), serverPlayer);
            }
        }
    }

    private Object2ObjectMap<UUID, InteractTreeInstance> getPlayerMap(UUID uuid) {
        ObjectOpenHashSet<PlayerPair> pairs = this.reference.get(uuid);
        if (pairs == null)
            return Object2ObjectMaps.emptyMap();

        Object2ObjectMap<UUID, InteractTreeInstance> map = new Object2ObjectOpenHashMap<>(pairs.size());
        for (PlayerPair pair : pairs) {
            if (this.relationship.containsKey(pair)) {
                map.put(pair.getOther(uuid), this.relationship.get(pair));
            }
        }
        return map;
    }

    private void loadInner(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        ThatSkyInteractions.LOGGER.info("Load TSI Data");

        this.setDirty(false);
        if (tree == null) {
            ThatSkyInteractions.LOGGER.warn("Data Error");
            return;
        }

        ListTag relationship = tag.getList(KEY_RELATIONSHIP, ListTag.TAG_COMPOUND);
        relationship.stream()
                .map(CompoundTag.class::cast)
                .forEach(this::loadEntry);
    }

    private void loadEntry(CompoundTag tag) {
        PlayerPair pair = PlayerPair.deserializeNBT(tag);
        InteractTreeInstance instance = new InteractTreeInstance(pair, this.tree, tag);
        this.relationship.put(pair, instance);
        this.refPari(pair);
    }

    private InteractTreeInstance create(PlayerPair pair) {
        InteractTreeInstance put = this.relationship.put(pair, new InteractTreeInstance(pair, tree, FRIEND_INTERACT_TREE));
        this.refPari(pair);
        return put;
    }

    private void refPari(PlayerPair pair) {
        this.reference.computeIfAbsent(pair.getLeft(), (k) -> new ObjectOpenHashSet<>()).add(pair);
        this.reference.computeIfAbsent(pair.getRight(), (k) -> new ObjectOpenHashSet<>()).add(pair);
    }

    public InteractTreeInstance get(PlayerPair pair) {
        this.setDirty();
        return this.relationship.computeIfAbsent(pair, this::create);
    }

    public InteractTree getTree() {
        return this.tree;
    }
}
