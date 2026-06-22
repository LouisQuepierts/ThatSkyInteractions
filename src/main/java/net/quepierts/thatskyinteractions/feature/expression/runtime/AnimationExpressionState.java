package net.quepierts.thatskyinteractions.feature.expression.runtime;

import lombok.*;

@Data
public class AnimationExpressionState implements ExpressionState {

    private Status  status = Status.RUNNING;
    private int     fsm;

    public enum Status {

        RUNNING,
        TRANSITING,
        FINISHED

    }

}
