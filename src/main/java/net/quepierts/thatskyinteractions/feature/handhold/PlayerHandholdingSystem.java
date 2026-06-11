package net.quepierts.thatskyinteractions.feature.handhold;

import lombok.experimental.UtilityClass;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.animation.fk.FKAnimation;
import net.quepierts.thatskyinteractions.feature.handhold.packet.ClientboundHandholdPacket;
import net.quepierts.thatskyinteractions.feature.registry.AnimationLayerTypes;
import net.quepierts.thatskyinteractions.feature.utils.PlayerUtils;
import org.jspecify.annotations.NonNull;

@UtilityClass
public class PlayerHandholdingSystem {

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

        final var lAttachment       = PlayerHandholdingAttachment.getAttachment(leader);
        final var fAttachment       = PlayerHandholdingAttachment.getAttachment(follower);

        final var lRelation         = lAttachment.getRelation();
        final var fRelation         = fAttachment.getRelation();

        if (lRelation.canLead(follower) && fRelation.canFollow(leader)) {

            final var hand = lAttachment.lead(follower);
            fAttachment.follow(leader, hand);

            PacketDistributor.sendToPlayer(
                    leader,
                    ClientboundHandholdPacket.lead(follower)
            );

            PacketDistributor.sendToPlayer(
                    follower,
                    ClientboundHandholdPacket.follow(leader)
            );

            play(leader, hand);
            play(follower, hand.opposite());

            return true;

        }

        return false;

    }

    public static void unhold(
            final @NonNull ServerPlayer     a,
            final @NonNull ServerPlayer     b
    ) {

        final var lAttachment       = PlayerHandholdingAttachment.getAttachment(a);
        final var fAttachment       = PlayerHandholdingAttachment.getAttachment(b);

        final var aHand = lAttachment.unhold(b);
        final var bHand = fAttachment.unhold(a);

        PacketDistributor.sendToPlayer(
                a,
                ClientboundHandholdPacket.unhold(b)
        );

        PacketDistributor.sendToPlayer(
                b,
                ClientboundHandholdPacket.unhold(a)
        );

        exit(a, aHand);
        exit(b, bHand);

    }

    public static void unholdAll(
            final @NonNull ServerPlayer     player
    ) {

        final var attachment        = PlayerHandholdingAttachment.getAttachment(player);
        final var left              = attachment.getRelation().getLeft();
        final var right             = attachment.getRelation().getRight();

        if (left != null) {
            PlayerHandholdingSystem.unhold(player, (ServerPlayer) left);
        }

        if (right != null) {
            PlayerHandholdingSystem.unhold(player, (ServerPlayer) right);
        }

    }

    public static Vec3 computeHandholdPosition(
            final @NonNull Player           leader,
            final          boolean          left
    ) {
        return PlayerUtils.getRelativePositionWorldSpace(leader, -0.2, left ? 1.0 : -1.0);
    }

    public static PlayerHandholdingAttachment getAttachment(
            final @NonNull Player player
    ) {
        return PlayerHandholdingAttachment.getAttachment(player);
    }

    private static void play(
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

    private static void exit(
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
