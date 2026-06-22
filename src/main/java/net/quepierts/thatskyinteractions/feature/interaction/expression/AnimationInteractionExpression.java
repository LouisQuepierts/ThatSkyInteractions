package net.quepierts.thatskyinteractions.feature.interaction.expression;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.core.interaction.DefaultInteractionFSM;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.animation.event.PlayerAnimationControllerEvent;
import net.quepierts.thatskyinteractions.feature.expression.AbstractAnimationExpression;
import net.quepierts.thatskyinteractions.feature.expression.runtime.AnimationExpressionState;
import net.quepierts.thatskyinteractions.feature.expression.runtime.ExpressionState;
import net.quepierts.thatskyinteractions.feature.interaction.PlayerInteractionSystem;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Slf4j
public class AnimationInteractionExpression
        extends AbstractAnimationExpression
        implements InteractionExpression {

    private final boolean       requester;
    private final boolean       continuous;

    public AnimationInteractionExpression(
            final Identifier    animation,
            final boolean       requester,
            final boolean       continuous
    ) {
        super(AUTO);
        this.animationId        = animation;
        this.requester          = requester;
        this.continuous         = continuous;
    }

    @Override
    public void onSignal(
            final @NonNull  ServerPlayer        player,
            final @Nullable ExpressionState     state,
            final           int                 signal
    ) {

        if (this.requester && signal == DefaultInteractionFSM.REQUESTER_ACCEPT) {
            PlayerAnimationSystem.signal(
                    player,
                    signal
            );
        }

    }

    @Override
    public void onCancel(
            final @NonNull  ServerPlayer            player
    ) {
        if (this.requester) {
            PlayerInteractionSystem.cancel(player, false);
        }
    }

    @Override
    public void onInterrupt(@NonNull final ServerPlayer player) {
        super.onInterrupt(player);
        this.onCancel(player);
    }

    @Override
    public boolean isInterruptible(
            final @NonNull  Player              player,
            final @Nullable ExpressionState     state
    ) {
        return (state != null)
                && ((AnimationExpressionState) state).getStatus() == AnimationExpressionState.Status.RUNNING;
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

        final var aState = (AnimationExpressionState) state;

        final var currentState = event.getCurrentState();
        if (this.requester) {

            if (currentState == DefaultInteractionFSM.REQUESTER_WAITING ||
                    this.continuous && currentState == DefaultInteractionFSM.REQUESTER_MAIN) {

                aState.setStatus(AnimationExpressionState.Status.RUNNING);

            } else if (currentState == DefaultInteractionFSM.REQUESTER_INVITE
                    || currentState == DefaultInteractionFSM.REQUESTER_CANCEL
                    || currentState == DefaultInteractionFSM.REQUESTER_ACCEPT) {

                aState.setStatus(AnimationExpressionState.Status.TRANSITING);
            }

        } else {

            if (this.continuous && currentState == DefaultInteractionFSM.RECEIVER_MAIN) {
                aState.setStatus(AnimationExpressionState.Status.RUNNING);
            } else {
                aState.setStatus(AnimationExpressionState.Status.TRANSITING);
            }

        }

    }

    @Override
    public void onGenerateData(
            @NonNull final Identifier   identifier,
                     final int          level
    ) {
        // do nothing first
    }
}
