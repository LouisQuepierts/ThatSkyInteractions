package net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline;

import org.jspecify.annotations.NonNull;

public interface SkeletonOutput {

    void accept(@NonNull SkeletonResultView view);

}
