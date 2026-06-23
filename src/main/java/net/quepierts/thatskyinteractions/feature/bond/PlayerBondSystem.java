package net.quepierts.thatskyinteractions.feature.bond;

import lombok.experimental.UtilityClass;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.animation.fk.FKAnimation;
import net.quepierts.thatskyinteractions.feature.bond.packet.ClientboundCarryPacket;
import net.quepierts.thatskyinteractions.feature.bond.packet.ClientboundHandholdPacket;
import net.quepierts.thatskyinteractions.feature.registry.AnimationLayerTypes;
import net.quepierts.thatskyinteractions.feature.utils.PlayerUtils;
import org.jspecify.annotations.NonNull;

@UtilityClass
public class PlayerBondSystem {

    private static final Identifier[] ANIMATIONS = new Identifier[] {
            FKAnimation.LEFT_ARM,
            FKAnimation.RIGHT_ARM
    };

    private static final Identifier[] LAYERS    = new Identifier[] {
            AnimationLayerTypes.LEFT_ARM.getId(),
            AnimationLayerTypes.RIGHT_ARM.getId()
    };

    public static boolean hold(
            final @NonNull ServerPlayer     leader,
            final @NonNull ServerPlayer     follower
    ) {

        if (leader.level() != follower.level()) {
            return false;
        }

        if (leader.distanceToSqr(follower) > 64 * 64) {
            return false;
        }

        final var lAttachment       = PlayerBondAttachment.getAttachment(leader);
        final var fAttachment       = PlayerBondAttachment.getAttachment(follower);

        final var lRelation         = lAttachment.getHandhold();
        final var fRelation         = fAttachment.getHandhold();

        if (lRelation.canLead(follower) && fRelation.canFollow(leader)) {

            final var hand = lAttachment.lead(follower);
            fAttachment.follow(leader, hand);

            PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                    leader,
                    ClientboundHandholdPacket.hold(leader, follower)
            );

            playHoldingHand(leader, hand);
            playHoldingHand(follower, hand.opposite());

            return true;

        }

        return false;

    }

    public static void unhold(
            final @NonNull ServerPlayer     a,
            final @NonNull ServerPlayer     b
    ) {

        final var lAttachment       = PlayerBondAttachment.getAttachment(a);
        final var fAttachment       = PlayerBondAttachment.getAttachment(b);

        final var aHand             = lAttachment.unhold(b.getUUID());
        final var bHand             = fAttachment.unhold(a.getUUID());

        PacketDistributor.sendToAllPlayers(
                ClientboundHandholdPacket.unhold(a, b)
        );

        exitHoldingHand(a, aHand);
        exitHoldingHand(b, bHand);

    }

    public static void unholdAll(
            final @NonNull ServerPlayer     player
    ) {

        final var attachment        = PlayerBondAttachment.getAttachment(player);
        final var left              = attachment.getHandhold().getLeft();
        final var right             = attachment.getHandhold().getRight();

        if (left != null) {
            PlayerBondSystem.unhold(player, (ServerPlayer) left);
        }

        if (right != null) {
            PlayerBondSystem.unhold(player, (ServerPlayer) right);
        }

    }

    public static boolean carry(
            final @NonNull ServerPlayer     carrier,
            final @NonNull ServerPlayer     rider
    ) {

        if (carrier == rider) {
            return false;
        }

        if (carrier.level() != rider.level()) {
            return false;
        }

        if (rider.distanceToSqr(rider) > 64 * 64) {
            return false;
        }

        final var cAttachment   = PlayerBondAttachment.getAttachment(carrier);
        final var rAttachment   = PlayerBondAttachment.getAttachment(rider);

        final var cRelation     = cAttachment.getCarry();
        final var rRelation     = rAttachment.getCarry();

        if (cRelation.canCarry(rider) && rRelation.canRide(carrier) && rider.getVehicle() == null) {
            cRelation.carry(rider);
            rRelation.ride(carrier);

            rider.startRiding(carrier, true, true);

            PacketDistributor.sendToPlayer(
                    carrier,
                    ClientboundCarryPacket.carry(rider)
            );

            PacketDistributor.sendToPlayer(
                    rider,
                    ClientboundCarryPacket.ride(carrier)
            );

            return true;
        }

        return false;

    }

    public static void unCarry(
            final @NonNull ServerPlayer     player
    ) {

        final var attachment    = PlayerBondSystem.getAttachment(player);
        final var relation      = attachment.getCarry();

        if (!relation.isCarrying()) {
            return;
        }

        final var carried       = relation.getCarried();
        relation.unCarry();

        if (!(carried instanceof ServerPlayer)) {
            return;
        }

        PacketDistributor.sendToPlayer(
                player,
                ClientboundCarryPacket.stopCarry(carried)
        );

        final var cRelation     = PlayerBondSystem.getAttachment(carried).getCarry();
        if (cRelation.getCarrier() != player) {
            return;
        }

        PacketDistributor.sendToPlayer(
                (ServerPlayer) carried,
                ClientboundCarryPacket.stopRide(player)
        );

        cRelation.unRide();

        if (carried.getVehicle() == player) {
            carried.stopRiding();
            PlayerAnimationSystem.abort(carried);
        }

    }

    public static void unRide(
            final @NonNull ServerPlayer     player
    ) {
        final var attachment    = PlayerBondSystem.getAttachment(player);
        final var relation      = attachment.getCarry();

        if (!relation.isBeingCarried()) {
            return;
        }

        final var carrier       = relation.getCarrier();
        relation.unRide();

        if (player.getVehicle() == carrier) {
            player.stopRiding();
            PlayerAnimationSystem.abort(player);
        }

        if (!(carrier instanceof ServerPlayer)) {
            return;
        }

        PacketDistributor.sendToPlayer(
                player,
                ClientboundCarryPacket.stopRide(carrier)
        );

        final var cRelation     = PlayerBondSystem.getAttachment(carrier).getCarry();
        if (cRelation.getCarried() != player) {
            return;
        }
        cRelation.unCarry();

        PacketDistributor.sendToPlayer(
                (ServerPlayer) carrier,
                ClientboundCarryPacket.stopCarry(player)
        );

    }

    public static boolean isBondedWith(
            final @NonNull Player           a,
            final @NonNull Player           b
    ) {

        final var attachment = PlayerBondSystem.getAttachment(a);

        return attachment.getHandhold().isHolding(b)
                || a == b.getVehicle()
                || b == a.getVehicle();

    }

    public static Vec3 computeHandholdPosition(
            final @NonNull Player           leader,
            final          boolean          left
    ) {
        return PlayerUtils.getRelativePositionWorldSpace(leader, -0.2, left ? 1.0 : -1.0);
    }

    public static PlayerBondAttachment getAttachment(
            final @NonNull Player player
    ) {
        return PlayerBondAttachment.getAttachment(player);
    }

    private static void playHoldingHand(
            final @NonNull ServerPlayer                     player,
            final PlayerHoldingHand hand
    ) {

        if (hand == PlayerHoldingHand.NONE) {
            return;
        }

        final var ordinal = hand.ordinal();
        PlayerAnimationSystem.play(
                player,
                ANIMATIONS[ordinal],
                LAYERS[ordinal]
        );

    }

    private static void exitHoldingHand(
            final @NonNull ServerPlayer                     player,
            final PlayerHoldingHand hand
    ) {
        if (hand == PlayerHoldingHand.NONE) {
            return;
        }

        PlayerAnimationSystem.exit(
                player,
                LAYERS[hand.ordinal()]
        );
    }

}
