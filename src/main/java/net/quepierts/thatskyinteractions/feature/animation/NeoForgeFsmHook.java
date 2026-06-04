package net.quepierts.thatskyinteractions.feature.animation;

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
        
    }

    @Override
    public void onTransitionEnd(
            final FSMState  fsmState,
            final int       fromState,
            final int       toState
    ) {

    }

    @Override
    public void onLoop(
            final FSMState  fsmState,
            final int       state
    ) {

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
