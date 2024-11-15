package net.quepierts.thatskyinteractions.common.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.quepierts.simpleanimator.core.network.IUpdate;
import net.quepierts.simpleanimator.core.network.NetworkPackets;
import net.quepierts.thatskyinteractions.common.data.attachment.UserDataAttachment;
import net.quepierts.thatskyinteractions.common.data.attachment.component.RelationshipComponent;

import java.util.UUID;

public class BlockPlayerPacket implements IUpdate {
    public static final Type<BlockPlayerPacket> TYPE = NetworkPackets.createType(BlockPlayerPacket.class);

    private final UUID target;
    private final boolean block;

    public BlockPlayerPacket(UUID target, boolean block) {
        this.target = target;
        this.block = block;
    }

    public BlockPlayerPacket(FriendlyByteBuf byteBuf) {
        this.target = byteBuf.readUUID();
        this.block = byteBuf.readBoolean();
    }

    @Override
    public void update(ServerPlayer serverPlayer) {
        RelationshipComponent relationship = UserDataAttachment.getAttachment(serverPlayer).getRelationship();

        if (block) {
            relationship.block(target);
        } else {
            relationship.unblock(target);
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(target);
        friendlyByteBuf.writeBoolean(block);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
