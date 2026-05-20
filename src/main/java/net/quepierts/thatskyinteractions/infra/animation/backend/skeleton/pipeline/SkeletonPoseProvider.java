package net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline;

import org.jspecify.annotations.NonNull;

public interface SkeletonPoseProvider {

    void fetch(
            @NonNull SkeletonContext    context,
            @NonNull SkeletonPoseBuffer target
    );

}
