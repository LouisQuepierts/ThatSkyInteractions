package net.quepierts.thatskyinteractions.client.data;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.data.PlayerPair;
import net.quepierts.thatskyinteractions.data.RelationshipSavedData;
import net.quepierts.thatskyinteractions.data.tree.InteractTree;
import net.quepierts.thatskyinteractions.data.tree.InteractTreeInstance;
import net.quepierts.thatskyinteractions.network.packet.BatchRelationshipPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class RelationshipDataCache {
    @NotNull private final Object2ObjectMap<UUID, InteractTreeInstance> relationship;
    @Nullable private InteractTree tree;

    public RelationshipDataCache() {
        this.relationship = new Object2ObjectOpenHashMap<>();
    }

    public void clear() {
        this.relationship.clear();
        this.tree = null;
    }

    public void handleUpdateRelationships(BatchRelationshipPacket batchRelationshipPacket) {
        ThatSkyInteractions.LOGGER.info("Update Relationships");
        this.tree = ThatSkyInteractions.getInstance().getProxy().getInteractTreeManager().get(RelationshipSavedData.FRIEND_INTERACT_TREE);
        this.relationship.putAll(batchRelationshipPacket.getRelationships());
    }

    public InteractTreeInstance get(UUID other) {
        PlayerPair pair = new PlayerPair(other, Minecraft.getInstance().player.getUUID());
        return relationship.computeIfAbsent(other, key -> new InteractTreeInstance(pair, tree, RelationshipSavedData.FRIEND_INTERACT_TREE));
    }

    public InteractTree getTree() {
        return tree;
    }

    public Object2ObjectMap<UUID, InteractTreeInstance> relationships() {
        return this.relationship;
    }

    public boolean isFriend(UUID player) {
        return this.relationship.containsKey(player);
    }
}
