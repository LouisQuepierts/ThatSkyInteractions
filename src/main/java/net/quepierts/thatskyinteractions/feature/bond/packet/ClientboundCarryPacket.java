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
import net.quepierts.thatskyinteractions.feature.bond.PlayerBondAttachment;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public record ClientboundCarryPacket(
        Operation       operation,
        UUID            carrier,
        UUID            rider
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
                    ClientboundCarryPacket::carrier,
                    UUIDUtil.STREAM_CODEC,
                    ClientboundCarryPacket::rider,
                    ClientboundCarryPacket::new
            );

    public static ClientboundCarryPacket carry(
            final @NonNull Player carrier,
            final @NonNull Player rider
    ) {
        return new ClientboundCarryPacket(Operation.CARRY, carrier.getUUID(), rider.getUUID());
    }

    public static ClientboundCarryPacket stop(
            final @NonNull Player carrier,
            final @NonNull Player rider
    ) {
        return new ClientboundCarryPacket(Operation.STOP, carrier.getUUID(), rider.getUUID());
    }


    @Override
    public void handleOnClient(final @NonNull Player player) {

        final var level         = player.level();
        
        final var carrier       = level.getPlayerByUUID(this.carrier());
        final var rider         = level.getPlayerByUUID(this.rider());
        


        switch (this.operation()) {

            case CARRY: {

                if (carrier == null || rider == null) {
                    return;
                }

                final var cRelation     = PlayerBondAttachment.getAttachment(carrier).getCarry();
                final var rRelation     = PlayerBondAttachment.getAttachment(rider).getCarry();

                cRelation.carry(rider);
                rRelation.ride(carrier);

                rider.startRiding(carrier, true, false);
                rider.refreshDimensions();

                break;
            }

            case STOP: {

                if (carrier != null) {
                    final var cRelation     = PlayerBondAttachment.getAttachment(carrier).getCarry();
                    cRelation.unCarry();
                }

                if (rider != null) {
                    final var rRelation     = PlayerBondAttachment.getAttachment(rider).getCarry();
                    rRelation.unRide();
                    rider.stopRiding();
                    rider.refreshDimensions();
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
        STOP;

        private static final Operation[] VALUES = values();

        public byte encode() {
            return (byte) this.ordinal();
        }

        public static Operation decode(final byte id) {
            return VALUES[id];
        }
    }

}
