package net.quepierts.thatskyinteractions.feature.expression;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.expression.packet.ExpressionControlPacket;
import net.quepierts.thatskyinteractions.feature.registry.AnimationLayerTypes;
import org.jspecify.annotations.NonNull;

@Slf4j
@UtilityClass
public class PlayerExpressionSystem {

    public static PlayerExpressionAttachment getAttachment(@NonNull Player player) {
        return PlayerExpressionAttachment.getAttachment(player);
    }

    public static boolean perform(
            final @NonNull  ServerPlayer    player,
            final @NonNull  Identifier      expressionId,
            final           int             level
    ) {
        final var manager       = PlayerExpressionManager.getInstance();
        final var expression    = manager.get(expressionId, level);

        if (expression == null) {
            return false;
        }

        final var attachment = getAttachment(player);

        if (attachment.isExpressing()) {
            interrupt(player);
            return false;
        }

        if (!expression.immediate()) {
            attachment.start(expression, expressionId);
        }
        expression.onPerform(player);

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                player,
                ExpressionControlPacket.perform(player.getUUID(), expressionId)
        );

        return true;
    }

    public static boolean perform(
            final @NonNull  ServerPlayer    player,
            final @NonNull  Identifier      expressionId
    ) {
        final var manager       = PlayerExpressionManager.getInstance();
        final var expression    = manager.get(expressionId);

        if (expression == null) {
            return false;
        }

        final var attachment = getAttachment(player);

        if (attachment.isExpressing()) {
            interrupt(player);
            return false;
        }

        if (!expression.immediate()) {
            attachment.start(expression, expressionId);
        }
        expression.onPerform(player);

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                player,
                ExpressionControlPacket.perform(player.getUUID(), expressionId)
        );

        return true;
    }

    public static void cancel(@NonNull ServerPlayer player) {
        final var attachment = getAttachment(player);
        final var currentId = attachment.getCurrent();

        if (currentId == null) {
            return;
        }

        attachment.clear();
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                player,
                ExpressionControlPacket.cancel(player.getUUID(), currentId)
        );
        PlayerAnimationSystem.abort(player, AnimationLayerTypes.DEFAULT.getId());
    }

    public static void interrupt(@NonNull ServerPlayer player) {
        final var attachment = getAttachment(player);
        final var currentId = attachment.getCurrent();

        if (currentId == null) {
            return;
        }

        final var expression = attachment.getReference().get();

        if (expression == null) {
            return;
        }

        if (expression.isInterruptible(player, attachment.getState())) {
            expression.onInterrupt(player);
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                    player,
                    ExpressionControlPacket.interrupt(player.getUUID(), currentId)
            );
        }
    }

    public static void finish(@NonNull ServerPlayer player) {
        final var attachment = getAttachment(player);
        final var currentId = attachment.getCurrent();

        if (currentId == null) {
            return;
        }

        final var expression = attachment.getReference().get();

        attachment.clear();

        if (expression != null) {
            expression.onFinished(player);
        }

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                player,
                ExpressionControlPacket.finished(player.getUUID(), currentId)
        );
    }

    public static boolean isPerforming(@NonNull ServerPlayer player) {
        return PlayerExpressionSystem   .getAttachment(player)
                                        .isExpressing();
    }
}
