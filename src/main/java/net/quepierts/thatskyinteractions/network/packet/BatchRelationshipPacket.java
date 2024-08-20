package net.quepierts.thatskyinteractions.network.packet;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.simpleanimator.core.network.ISync;
import net.quepierts.simpleanimator.core.network.NetworkPackets;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.data.tree.InteractTreeInstance;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class BatchRelationshipPacket implements ISync {
    public static final Type<BatchRelationshipPacket> TYPE = NetworkPackets.createType(BatchRelationshipPacket.class);

    private final Object2ObjectMap<UUID, InteractTreeInstance> map;
    public BatchRelationshipPacket(FriendlyByteBuf byteBuf) {
        map = byteBuf.readMap(
                Object2ObjectOpenHashMap::new,
                byteBuf1 -> byteBuf1.readUUID(),
                InteractTreeInstance::fromNetwork
        );
    }

    public BatchRelationshipPacket(Object2ObjectMap<UUID, InteractTreeInstance> relationship) {
        this.map = relationship;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void sync() {
        ThatSkyInteractions.getInstance().getClient().getCache().handleUpdateRelationships(this);
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(this.map.size());
        for (Map.Entry<UUID, InteractTreeInstance> entry : this.map.entrySet()) {
            friendlyByteBuf.writeUUID(entry.getKey());
            entry.getValue().toNetwork(friendlyByteBuf);
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public Object2ObjectMap<UUID, InteractTreeInstance> getRelationships() {
        return new Object2ObjectOpenHashMap<>(this.map);
    }
}
