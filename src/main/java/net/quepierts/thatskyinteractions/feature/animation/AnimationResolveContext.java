package net.quepierts.thatskyinteractions.feature.animation;

import net.quepierts.thatskyinteractions.feature.animation.fk.FKTargetType;
import net.quepierts.veynir.backend.skeleton.pipeline.SkeletonPoseProvider;
import org.jspecify.annotations.NonNull;

public interface AnimationResolveContext {

    @NonNull SkeletonPoseProvider getPoseProvider();

    void setFkConfiguration(
            final @NonNull FKTargetType type,
            final          float        weight,
            final          boolean      active
    );

}
