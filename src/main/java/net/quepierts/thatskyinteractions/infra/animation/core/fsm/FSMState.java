package net.quepierts.thatskyinteractions.infra.animation.core.fsm;

import lombok.Getter;
import lombok.Setter;

@Getter
public final class FSMState {

    float           elapsed;
    float           blendElapsed;
    float           blendDuration;

    float           normalizedElapsed;
    float           normalizedBlendElapsed;

    int             lastState;
    int             currentState;

    boolean         blending;
    boolean         finished;

    @Setter
    @Getter
    FSMParameter    uniform;

}
