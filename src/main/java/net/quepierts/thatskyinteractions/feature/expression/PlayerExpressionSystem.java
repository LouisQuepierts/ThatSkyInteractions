package net.quepierts.thatskyinteractions.feature.expression;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.quepierts.thatskyinteractions.feature.expression.packet.ExpressionControlPacket;
import org.jspecify.annotations.NonNull;

@Slf4j
@UtilityClass
public class PlayerExpressionSystem {

    public static PlayerExpressionAttachment getAttachment(@NonNull ServerPlayer player) {
        return PlayerExpressionAttachment.getAttachment(player);
    }

    public static void perform(
            final @NonNull  ServerPlayer    player,
            final @NonNull  Identifier      expressionId,
            final           int             level
    ) {
        final var manager       = PlayerExpressionManager.getInstance();
        final var expression    = manager.get(expressionId, level);

        if (expression == null) {
            return;
        }

        final var attachment = getAttachment(player);

        if (attachment.isExpressing()) {
            cancel(player);
        }

        if (!expression.immediate()) {
            attachment.start(expressionId, player.level().getGameTime());
        }
        expression.onPerform(player);

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                player,
                ExpressionControlPacket.perform(player.getUUID(), expressionId)
        );
    }

    public static void cancel(@NonNull ServerPlayer player) {
        final var attachment = getAttachment(player);
        final var currentId = attachment.getCurrent();

        if (currentId == null) {
            return;
        }

        final var manager = PlayerExpressionManager.getInstance();
        final var expression = manager.get(currentId);

        attachment.clear();

        if (expression != null) {
            expression.onCancel(player);
        }

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                player,
                ExpressionControlPacket.cancel(player.getUUID(), currentId)
        );
    }

    public static void finish(@NonNull ServerPlayer player) {
        final var attachment = getAttachment(player);
        final var currentId = attachment.getCurrent();

        if (currentId == null) {
            return;
        }

        final var manager = PlayerExpressionManager.getInstance();
        final var expression = manager.get(currentId);

        attachment.clear();

        if (expression != null) {
            expression.onFinished(player);
        }

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                player,
                ExpressionControlPacket.finished(player.getUUID(), currentId)
        );
    }

    public static void interrupt(@NonNull ServerPlayer player) {
        final var attachment = getAttachment(player);
        final var currentId = attachment.getCurrent();

        if (currentId == null) {
            return;
        }

        final var manager = PlayerExpressionManager.getInstance();
        final var expression = manager.get(currentId);

        attachment.clear();

        if (expression != null) {
            expression.onInterrupted(player);
        }

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                player,
                ExpressionControlPacket.cancel(player.getUUID(), currentId)
        );
    }
}
