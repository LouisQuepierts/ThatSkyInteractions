package net.quepierts.thatskyinteractions.core.interaction;

import com.google.common.collect.ImmutableList;
import lombok.experimental.UtilityClass;
import net.quepierts.veynir.core.fsm.FiniteStateMachine;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftFSM;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@UtilityClass
public class DefaultInteractionFSM {

    public static final FiniteStateMachine REQUESTER = FiniteStateMachine.compiler()
            .sequence()

            .withState("system#enter")
            .withState("invite")
            .withState("waiting")
            .withState("cancel")
            .withState("accept")
            .withState("main")
            .withState("exit")
            .withState("system#exit")

            .withTransition("waiting", "waiting")
            .withTransition("cancel", "system#exit")

            .withHook(DefaultMinecraftFSM.HOOK)
            .compile();

    public static final FiniteStateMachine RECEIVER = FiniteStateMachine.compiler()
            .sequence()

            .withState("system#enter")
            .withState("accept")
            .withState("main")
            .withState("exit")
            .withState("system#exit")

            .withHook(DefaultMinecraftFSM.HOOK)
            .compile();

    public static final List<String> REQUESTER_STATES   = ImmutableList.of("invite", "waiting", "cancel", "accept", "main", "exit");
    public static final int REQUESTER_INVITE            = REQUESTER.getLookup().find("invite");
    public static final int REQUESTER_WAITING           = REQUESTER.getLookup().find("waiting");
    public static final int REQUESTER_CANCEL            = REQUESTER.getLookup().find("cancel");
    public static final int REQUESTER_ACCEPT            = REQUESTER.getLookup().find("accept");
    public static final int REQUESTER_MAIN              = REQUESTER.getLookup().find("main");
    public static final int REQUESTER_EXIT              = REQUESTER.getLookup().find("exit");

    public static final List<String> RECEIVER_STATES   = ImmutableList.of("accept", "main", "exit");
    public static final int RECEIVER_ACCEPT             = RECEIVER.getLookup().find("accept");
    public static final int RECEIVER_MAIN               = RECEIVER.getLookup().find("main");
    public static final int RECEIVER_EXIT               = RECEIVER.getLookup().find("exit");

}
