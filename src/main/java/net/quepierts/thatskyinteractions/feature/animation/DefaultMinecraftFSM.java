package net.quepierts.thatskyinteractions.feature.animation;

import lombok.experimental.UtilityClass;
import net.quepierts.thatskyinteractions.infra.animation.core.fsm.FiniteStateMachine;

@UtilityClass
public class DefaultMinecraftFSM {

    public static final FiniteStateMachine SEQUENCE = FiniteStateMachine.compiler()
            .withState("enter")
            .withState("main")
            .withState("exit")

            .withTransition("enter", "main")
            .withTransition("main", "exit")

            .withInitialState("enter")
            .withTerminalState("exit")

            .compile();

}
