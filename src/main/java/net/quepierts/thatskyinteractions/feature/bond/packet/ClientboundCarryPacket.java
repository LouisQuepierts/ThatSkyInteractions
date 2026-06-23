package net.quepierts.thatskyinteractions.feature.bond.packet;

import dev.anvilcraft.lib.v2.network.packet.IClientboundPacket;
import dev.anvilcraft.lib.v2.network.packet.IPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.bond.PlayerBondSystem;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public record ClientboundCarryPacket(
        Operation       operation,
        UUID            other
) implements IClientboundPacket {

    public static final Type<ClientboundCarryPacket> TYPE
            = IPacket.type(ThatSkyInteractions.location("carry/controler"));

    public static final StreamCodec<ByteBuf, ClientboundCarryPacket> STREAM_CODEC
            = StreamCodec.composite(
                    ByteBufCodecs.BYTE.map(
                            Operation::decode,
                            Operation::encode
                    ),
                    ClientboundCarryPacket::operation,
                    UUIDUtil.STREAM_CODEC,
                    ClientboundCarryPacket::other,
                    ClientboundCarryPacket::new
            );

    public static ClientboundCarryPacket carry(
            final @NonNull Player other
    ) {
        return new ClientboundCarryPacket(Operation.CARRY, other.getUUID());
    }

    public static ClientboundCarryPacket ride(
            final @NonNull Player other
    ) {
        return new ClientboundCarryPacket(Operation.RIDE, other.getUUID());
    }

    public static ClientboundCarryPacket stopCarry(
            final @NonNull Player other
    ) {
        return new ClientboundCarryPacket(Operation.STOP_CARRY, other.getUUID());
    }

    public static ClientboundCarryPacket stopRide(
            final @NonNull Player other
    ) {
        return new ClientboundCarryPacket(Operation.STOP_RIDE, other.getUUID());
    }


    @Override
    public void handleOnClient(final @NonNull Player player) {

        final var level     = player.level();
        final var other     = level.getPlayerByUUID(this.other);

        switch (this.operation()) {

            case CARRY: {
                if (other != null) {

                    final var attachment    = PlayerBondSystem.getAttachment(player);
                    final var relation      = attachment.getCarry();

                    relation.carry(other);
                    other.startRiding(player, true, false);

                }
                break;
            }

            case RIDE: {
                if (other != null) {

                    final var attachment    = PlayerBondSystem.getAttachment(player);
                    final var relation      = attachment.getCarry();

                    relation.ride(other);
                    player.startRiding(other, true, false);

                }
                break;
            }

            case STOP_CARRY: {

                final var attachment    = PlayerBondSystem.getAttachment(player);
                final var relation      = attachment.getCarry();

                relation.unCarry();
                if (other != null && other.getVehicle() == player) {
                    other.stopRiding();
                }

                break;
            }

            case STOP_RIDE: {

                final var attachment    = PlayerBondSystem.getAttachment(player);
                final var relation      = attachment.getCarry();

                relation.unRide();
                if (player.getVehicle() == other) {
                    player.stopRiding();
                }

                break;
            }

        }

    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum Operation {
        CARRY,
        RIDE,
        STOP_CARRY,
        STOP_RIDE;

        private static final Operation[] VALUES = values();

        public byte encode() {
            return (byte) this.ordinal();
        }

        public static Operation decode(final byte id) {
            return VALUES[id];
        }
    }

}
