package net.quepierts.thatskyinteractions.feature.animation.humanoid;

import net.quepierts.animata4j.core.skeleton.PoseCache;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerAnimationDefinition;
import net.quepierts.thatskyinteractions.feature.animation.HumanoidAnimationState;
import net.quepierts.animata4j.backend.execution.ExecutionState;
import net.quepierts.animata4j.backend.skeleton.pipeline.SkeletonPoseProvider;
import net.quepierts.animata4j.core.fsm.FSMState;
import net.quepierts.animata4j.core.fsm.FiniteStateMachine;
import org.jspecify.annotations.NonNull;

public interface PlayerAnimation {

    static PlayerAnimation simple(@NonNull PlayerAnimationDefinition definition) {
        return TemplateAnimation.single(definition);
    }

    static PlayerAnimation sequence(@NonNull PlayerAnimationDefinition definition) {
        return TemplateAnimation.sequence(definition);
    }

    void play(
            @NonNull FSMState               fsmState
    );

    void update(
            @NonNull FSMState               fsmState,
            float                           delta
    );

    void resolve(
            @NonNull FSMState               fsmState,
            @NonNull ExecutionState         executionState,
            @NonNull HumanoidAnimationState animationState,
            @NonNull PoseCache              target,
            @NonNull SkeletonPoseProvider   provider
    );

    void cleanup(
            @NonNull FSMState               fsmState
    );

    void event(
            @NonNull FSMState               fsmState,
            final int                       event
    );

    void abort(
            @NonNull FSMState               fsmState
    );

    void exit(
            @NonNull FSMState               fsmState
    );

    boolean isLooping(
            @NonNull FSMState               fsmState
    );

    FiniteStateMachine getFsm();
}
