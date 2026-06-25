package net.quepierts.thatskyinteractions.feature.interaction.expression;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftFSM;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.animation.event.PlayerAnimationControllerEvent;
import net.quepierts.thatskyinteractions.feature.expression.AbstractAnimationExpression;
import net.quepierts.thatskyinteractions.feature.expression.runtime.AnimationExpressionState;
import net.quepierts.thatskyinteractions.feature.expression.runtime.ExpressionState;
import net.quepierts.thatskyinteractions.feature.registry.AnimationLayerTypes;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class RiderExpression
        extends AbstractAnimationExpression
        implements InteractionExpression {

    public RiderExpression(final Identifier animation) {
        super(AUTO);
        this.animationId = animation;
    }

    @Override
    public void onPerform(final @NonNull ServerPlayer player) {
        PlayerAnimationSystem.play(player, this.animationId, AnimationLayerTypes.DEFAULT.getId());
    }

    @Override
    public boolean isRestrictMotion(
            final @NonNull  Player          player,
            final @Nullable ExpressionState state
    ) {
        return (state != null)
                && ((AnimationExpressionState) state).getStatus() == AnimationExpressionState.Status.TRANSITING;
    }

    @Override
    public boolean isRestrictCamera(
            final @NonNull  Player          player,
            final @Nullable ExpressionState state
    ) {
        return (state != null)
                && ((AnimationExpressionState) state).getStatus() == AnimationExpressionState.Status.TRANSITING;
    }

    @Override
    public void onAnimationTransitionStart(
            final @NonNull  Player                                      player,
            final @Nullable ExpressionState                             state,
            final PlayerAnimationControllerEvent.State.TransitionStart  event
    ) {

        if (state == null) {
            return;
        }

        var status = event.getCurrentState() == DefaultMinecraftFSM.CONTINUOUS_MAIN
                ? AnimationExpressionState.Status.RUNNING
                : AnimationExpressionState.Status.TRANSITING;

        ((AnimationExpressionState) state).setStatus(status);

    }

    @Override
    public void onGenerateData(
            @NonNull final Identifier identifier,
            final int          level
    ) {
        // do nothing first
    }
}
