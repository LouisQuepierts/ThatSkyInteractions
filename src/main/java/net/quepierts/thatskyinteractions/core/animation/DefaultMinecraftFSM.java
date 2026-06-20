package net.quepierts.thatskyinteractions.core.animation;

import lombok.experimental.UtilityClass;
import net.quepierts.veynir.core.fsm.FSMHook;
import net.quepierts.veynir.core.fsm.FiniteStateMachine;
import net.quepierts.thatskyinteractions.infra.Services;

@UtilityClass
public class DefaultMinecraftFSM {

    public static final FSMHook             HOOK            = Services.load(TsiFsmHook.class);

    public static final FiniteStateMachine  SINGLE          = FiniteStateMachine.compiler()
            .sequence()

            .withState("system#enter")
            .withState("main")
            .withState("system#exit")

            .withHook(HOOK)
            .compile();

    public static final int                 SINGLE_MAIN     = SINGLE.getLookup().find("main");

    public static final FiniteStateMachine  SEQUENCE        = FiniteStateMachine.compiler()
            .sequence()

            .withState("system#enter")
            .withState("enter")
            .withState("main")
            .withState("exit")
            .withState("system#exit")

            .withHook(HOOK)
            .compile();

    public static final int                 SEQUENCE_ENTER    = SEQUENCE.getLookup().find("enter");
    public static final int                 SEQUENCE_MAIN     = SEQUENCE.getLookup().find("main");
    public static final int                 SEQUENCE_EXIT     = SEQUENCE.getLookup().find("exit");

    public static final FiniteStateMachine  CONTINUOUS          = FiniteStateMachine.compiler()
            .sequence()

            .withState("system#enter")
            .withState("enter")
            .withState("main")
            .withState("exit")
            .withState("system#exit")

            .withTransition("main", "main")

            .withHook(HOOK)
            .compile();

    public static final int                 CONTINUOUS_ENTER    = CONTINUOUS.getLookup().find("enter");
    public static final int                 CONTINUOUS_MAIN     = CONTINUOUS.getLookup().find("main");
    public static final int                 CONTINUOUS_EXIT     = CONTINUOUS.getLookup().find("exit");

    public static final FiniteStateMachine  FORWARD_KINEMATICS  = FiniteStateMachine.compiler()
            .sequence()

            .withState("system#enter")
            .withState("main")
            .withState("system#exit")

            .withTransition("main", "main")

            .withHook(HOOK)
            .compile();

    public static final int                 FK_MAIN             = FORWARD_KINEMATICS.getLookup().find("main");

}
