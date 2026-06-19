package net.quepierts.thatskyinteractions.feature.animation;

import net.neoforged.neoforge.common.NeoForge;
import net.quepierts.thatskyinteractions.feature.animation.event.PlayerAnimationControllerEvent;
import net.quepierts.veynir.core.fsm.FSMState;
import net.quepierts.thatskyinteractions.core.animation.TsiFsmHook;

public final class NeoForgeFsmHook implements TsiFsmHook {

    @Override
    public void onTransitionStart(
            final FSMState  fsmState,
            final int       fromState,
            final int       toState,
            final int       triggerType,
            final int       triggerId
    ) {
        // generally, the attachment here will be the AnimationLayer
        final var layer      = (AnimationLayer) fsmState.getAttachment();

        NeoForge.EVENT_BUS.post(new PlayerAnimationControllerEvent.State.TransitionStart(
                layer.getParent(),
                layer.getType(),
                fromState,
                toState
        ));
    }

    @Override
    public void onTransitionEnd(
            final FSMState  fsmState,
            final int       fromState,
            final int       toState
    ) {
        final var layer      = (AnimationLayer) fsmState.getAttachment();

        NeoForge.EVENT_BUS.post(new PlayerAnimationControllerEvent.State.TransitionEnd(
                layer.getParent(),
                layer.getType(),
                fromState,
                toState
        ));
    }

    @Override
    public void onLoop(
            final FSMState  fsmState,
            final int       state
    ) {
        final var layer      = (AnimationLayer) fsmState.getAttachment();

        NeoForge.EVENT_BUS.post(new PlayerAnimationControllerEvent.State.Loop(
                layer.getParent(),
                layer.getType(),
                state
        ));
    }

    @Override
    public void onStart(
            final FSMState  fsmState
    ) {

    }

    @Override
    public void onFinish(
            final FSMState  fsmState
    ) {

    }

}
