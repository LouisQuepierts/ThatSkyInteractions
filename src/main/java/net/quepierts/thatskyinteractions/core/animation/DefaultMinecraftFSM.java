package net.quepierts.thatskyinteractions.core.animation;

import lombok.experimental.UtilityClass;
import net.quepierts.animata4j.core.fsm.FiniteStateMachine;

@UtilityClass
public class DefaultMinecraftFSM {

    public static final FiniteStateMachine SINGLE = FiniteStateMachine.compiler()
            .sequence()

            .withState("system#enter")
            .withState("main")
            .withState("system#exit")

            .compile();

    public static final FiniteStateMachine SEQUENCE = FiniteStateMachine.compiler()
            .sequence()

            .withState("system#enter")
            .withState("enter")
            .withState("main")
            .withState("exit")
            .withState("system#exit")

            .compile();

}
