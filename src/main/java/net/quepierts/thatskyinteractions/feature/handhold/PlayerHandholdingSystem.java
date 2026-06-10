package net.quepierts.thatskyinteractions.feature.handhold;

import lombok.experimental.UtilityClass;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.quepierts.thatskyinteractions.feature.handhold.packet.ClientboundHandholdPacket;
import net.quepierts.thatskyinteractions.feature.utils.PlayerUtils;
import org.jspecify.annotations.NonNull;

@UtilityClass
public class PlayerHandholdingSystem {

    public static boolean hold(
            final @NonNull ServerPlayer     leader,
            final @NonNull ServerPlayer     follower
    ) {

        final var lAttachment       = PlayerHandholdingAttachment.getAttachment(leader);
        final var fAttachment       = PlayerHandholdingAttachment.getAttachment(follower);

        if (lAttachment.canLead(follower) && fAttachment.canFollow(leader)) {

            lAttachment.lead(follower);
            fAttachment.follow(leader);

            PacketDistributor.sendToPlayer(
                    leader,
                    ClientboundHandholdPacket.lead(follower)
            );

            PacketDistributor.sendToPlayer(
                    follower,
                    ClientboundHandholdPacket.follow(leader)
            );

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

        lAttachment.unhold(b);
        fAttachment.unhold(a);

        PacketDistributor.sendToPlayer(
                a,
                ClientboundHandholdPacket.unhold(b)
        );

        PacketDistributor.sendToPlayer(
                b,
                ClientboundHandholdPacket.unhold(a)
        );

    }

    public static void unholdAll(
            final @NonNull ServerPlayer     player
    ) {

        final var attachment        = PlayerHandholdingAttachment.getAttachment(player);
        final var left              = attachment.getLeft();
        final var right             = attachment.getRight();

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

}
