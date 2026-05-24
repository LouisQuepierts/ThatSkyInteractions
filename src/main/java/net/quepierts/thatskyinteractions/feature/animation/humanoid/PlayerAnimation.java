package net.quepierts.thatskyinteractions.feature.animation.humanoid;

import net.quepierts.thatskyinteractions.core.model.animation.PlayerAnimationDefinition;
import net.quepierts.thatskyinteractions.feature.animation.HumanoidAnimationState;
import net.quepierts.thatskyinteractions.infra.animation.backend.execution.ExecutionState;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline.SkeletonPoseProvider;
import net.quepierts.thatskyinteractions.infra.animation.core.fsm.FSMState;
import org.jspecify.annotations.NonNull;

public interface PlayerAnimation {

    static PlayerAnimation simple(@NonNull PlayerAnimationDefinition definition) {
        return SequencePlayerAnimation.parse(definition);
    }

    void play(
            @NonNull FSMState state
    );

    void update(
            @NonNull FSMState state,
            float delta
    );

    void resolve(
            @NonNull FSMState               fsmState,
            @NonNull ExecutionState         executionState,
            @NonNull HumanoidAnimationState animationState,
            @NonNull SkeletonPoseProvider   provider
    );

    void cleanup(
            @NonNull FSMState state
    );
}
