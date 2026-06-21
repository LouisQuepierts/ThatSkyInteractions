package net.quepierts.thatskyinteractions.feature.bond.packet;

import dev.anvilcraft.lib.v2.network.packet.IClientboundPacket;
import dev.anvilcraft.lib.v2.network.packet.IPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.animation.fk.FKTargetType;
import net.quepierts.thatskyinteractions.feature.bond.PlayerBondSystem;
import net.quepierts.thatskyinteractions.feature.bond.PlayerHoldingHand;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public record ClientboundHandholdPacket(
        Operation       operation,
        UUID            leader,
        UUID            follower
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
                    ClientboundHandholdPacket::leader,
                    UUIDUtil.STREAM_CODEC,
                    ClientboundHandholdPacket::follower,
                    ClientboundHandholdPacket::new
            );

    public static ClientboundHandholdPacket hold(
            final @NonNull Player leader,
            final @NonNull Player follower
    ) {
        return new ClientboundHandholdPacket(Operation.HOLD, leader.getUUID(), follower.getUUID());
    }

    public static ClientboundHandholdPacket unhold(
            final @NonNull Player leader,
            final @NonNull Player follower
    ) {
        return new ClientboundHandholdPacket(Operation.UNHOLD, leader.getUUID(), follower.getUUID());
    }

    @Override
    public void handleOnClient(final @NonNull Player player) {

        final var level         = player.level();

        final var leader        = level.getPlayerByUUID(this.leader);
        final var follower      = level.getPlayerByUUID(this.follower);

        switch (this.operation()) {
            case HOLD: {

                if (leader == null || follower == null) {
                    return;
                }

                final var lAttachment   = PlayerBondSystem.getAttachment(leader);
                final var fAttachment   = PlayerBondSystem.getAttachment(follower);

                final var hand = lAttachment.lead(follower);
                fAttachment.follow(leader, hand);

                setupFk(leader, follower, hand);
                setupFk(follower, leader, hand.opposite());
                break;
            }
            case UNHOLD: {

                if (leader != null) {
                    final var lAttachment = PlayerBondSystem.getAttachment(leader);
                    clearFk(leader, lAttachment.unhold(this.follower()));
                }

                if (follower != null) {
                    final var fAttachment = PlayerBondSystem.getAttachment(follower);
                    clearFk(follower, fAttachment.unhold(this.leader()));
                }
                break;
            }
        }

    }

    private static void setupFk(
            final @NonNull Player               player,
            final @NonNull Player               other,
            final @NonNull PlayerHoldingHand    hand
    ) {
        if (hand == PlayerHoldingHand.NONE) {
            return;
        }

        final var controller    = PlayerAnimationSystem.getAnimationData(player).getController();
        final var type          = map(hand);
        controller.getFkController().setTarget(
                type,
                (out, partialTick) -> {
                    final var px = Mth.lerp(partialTick, player.xOld, player.getX());
                    final var py = Mth.lerp(partialTick, player.yOld, player.getY());
                    final var pz = Mth.lerp(partialTick, player.zOld, player.getZ());
                    final var ox = Mth.lerp(partialTick, other.xOld, other.getX());
                    final var oy = Mth.lerp(partialTick, other.yOld, other.getY());
                    final var oz = Mth.lerp(partialTick, other.zOld, other.getZ());

                    // center
                    out.set(
                            (px + ox) * 0.5f,
                            (py + oy) * 0.5f + 0.375f,
                            (pz + oz) * 0.5f
                    );
                }
        );
    }

    private static void clearFk(
            final @NonNull Player               player,
            final @NonNull PlayerHoldingHand    hand
    ) {
        if (hand == PlayerHoldingHand.NONE) {
            return;
        }

        final var controller    = PlayerAnimationSystem.getAnimationData(player).getController();
        final var fkController = controller.getFkController();
        final var type = map(hand);
        fkController.clearTarget(type);
        fkController.setConfiguration(type, 0.0f, false);
    }

    private static FKTargetType map(final @NonNull PlayerHoldingHand hand) {
        return hand == PlayerHoldingHand.LEFT ? FKTargetType.LEFT_ARM : FKTargetType.RIGHT_ARM;
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum Operation {
        HOLD,
        UNHOLD;

        public byte encode() {
            return (byte) this.ordinal();
        }

        public static Operation decode(final byte id) {
            return switch (id) {
                case 0 -> HOLD;
                case 1 -> UNHOLD;
                default -> throw new IllegalArgumentException("Invalid operation id: " + id);
            };
        }
    }
}
