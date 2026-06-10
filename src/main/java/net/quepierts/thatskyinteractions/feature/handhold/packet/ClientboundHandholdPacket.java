package net.quepierts.thatskyinteractions.feature.handhold.packet;

import dev.anvilcraft.lib.v2.network.packet.IClientboundPacket;
import dev.anvilcraft.lib.v2.network.packet.IPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.handhold.PlayerHandholdingSystem;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public record ClientboundHandholdPacket(
        Operation       operation,
        UUID            uuid
) implements IClientboundPacket {

    public static final Type<ClientboundHandholdPacket> TYPE
            = IPacket.type(ThatSkyInteractions.location("handhold/controler"));

    public static final StreamCodec<ByteBuf, ClientboundHandholdPacket> STREAM_CODEC
            = StreamCodec.composite(
                    ByteBufCodecs.BYTE.map(
                            Operation::decode,
                            Operation::encode
                    ),
                    ClientboundHandholdPacket::operation,
                    UUIDUtil.STREAM_CODEC,
                    ClientboundHandholdPacket::uuid,
                    ClientboundHandholdPacket::new
            );

    public static ClientboundHandholdPacket lead(
            final @NonNull Player follower
    ) {
        return new ClientboundHandholdPacket(Operation.LEAD, follower.getUUID());
    }

    public static ClientboundHandholdPacket follow(
            final @NonNull Player leader
    ) {
        return new ClientboundHandholdPacket(Operation.FOLLOW, leader.getUUID());
    }

    public static ClientboundHandholdPacket unhold(
            final @NonNull Player other
    ) {
        return new ClientboundHandholdPacket(Operation.UNHOLD, other.getUUID());
    }

    @Override
    public void handleOnClient(final @NonNull Player player) {

        final var level         = player.level();
        final var other         = level.getPlayerByUUID(this.uuid);

        if (other == null) {
            return;
        }

        final var lAttachment   = PlayerHandholdingSystem.getAttachment(player);
        final var oAttachment   = PlayerHandholdingSystem.getAttachment(other);

        switch (this.operation()) {
            case LEAD: {
                lAttachment.lead(other);
                oAttachment.follow(player);
                break;
            }
            case FOLLOW: {
                lAttachment.follow(other);
                oAttachment.lead(player);
                break;
            }
            case UNHOLD: {
                lAttachment.unhold(other);
                oAttachment.unhold(player);
                break;
            }
        }

    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum Operation {
        LEAD,
        FOLLOW,
        UNHOLD;

        public byte encode() {
            return (byte) this.ordinal();
        }

        public static Operation decode(final byte id) {
            return switch (id) {
                case 0 -> LEAD;
                case 1 -> FOLLOW;
                case 2 -> UNHOLD;
                default -> throw new IllegalArgumentException("Invalid operation id: " + id);
            };
        }
    }
}
