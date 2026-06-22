package net.quepierts.thatskyinteractions.feature.interaction.expression;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.feature.expression.runtime.ExpressionState;
import net.quepierts.thatskyinteractions.feature.interaction.PlayerInteractionSystem;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public final class DefaultInteractionExpression implements InteractionExpression {

    @Getter
    public final boolean isRequester;

    public static @NonNull DefaultInteractionExpression requester() {
        return new DefaultInteractionExpression(true);
    }

    public static @NonNull DefaultInteractionExpression receiver() {
        return new DefaultInteractionExpression(false);
    }

    @Override
    public void onPerform(
            final @NonNull  ServerPlayer            player
    ) {
    }

    @Override
    public void onCancel(
            final @NonNull  ServerPlayer            player
    ) {
        if (this.isRequester()) {
            PlayerInteractionSystem.cancel(player);
        }
    }

    @Override
    public boolean isFinished(
            final @NonNull  Player                  player,
            final @Nullable ExpressionState         state
    ) {
        final var attachment = PlayerInteractionSystem.getInteractionAttachment(player);
        return attachment.getOngoing() == null;
    }

    @Override
    public boolean isInterruptible(
            final @NonNull  Player                  player,
            final @Nullable ExpressionState         state
    ) {
        return false;
    }

    @Override
    public boolean immediate() {
        return false;
    }
}
