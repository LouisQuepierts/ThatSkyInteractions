package net.quepierts.thatskyinteractions.infra.animation.core.fsm;

import lombok.Getter;
import lombok.Setter;

public final class FSMState {

    @Getter
    float           elapsed;
    @Getter
    float           blendElapsed;
    float           blendDuration;

    @Getter
    int             lastState;
    @Getter
    int             currentState;

    @Getter
    boolean         blending;

    @Getter
    boolean         finished;

    @Setter
    @Getter
    FSMParameter    uniform;

}
