package net.quepierts.thatskyinteractions.feature.handhold.packet;

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
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationController;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.animation.fk.FKTargetSupplier;
import net.quepierts.thatskyinteractions.feature.animation.fk.FKTargetType;
import net.quepierts.thatskyinteractions.feature.handhold.PlayerHandholdingSystem;
import net.quepierts.thatskyinteractions.feature.handhold.PlayerHoldingHand;
import org.joml.Vector3f;
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
                final var hand = lAttachment.lead(other);
                oAttachment.follow(player, hand);

                setupFk(player, other, hand);
                setupFk(other, player, hand.opposite());
                break;
            }
            case FOLLOW: {
                final var hand = oAttachment.lead(player);
                lAttachment.follow(other, hand);

                setupFk(other, player, hand);
                setupFk(player, other, hand.opposite());
                break;
            }
            case UNHOLD: {
                clearFk(player, lAttachment.unhold(other));
                clearFk(other, oAttachment.unhold(player));
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
        controller.getFkController().clearTarget(map(hand));
    }

    private static FKTargetType map(final @NonNull PlayerHoldingHand hand) {
        return hand == PlayerHoldingHand.LEFT ? FKTargetType.LEFT_ARM : FKTargetType.RIGHT_ARM;
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
