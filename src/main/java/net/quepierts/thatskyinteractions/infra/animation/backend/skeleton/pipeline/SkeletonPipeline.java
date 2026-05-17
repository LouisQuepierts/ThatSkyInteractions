package net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline;

public interface SkeletonPipeline {

    static DefaultSkeletonPipelineImpl.Compiler compiler() {
        return DefaultSkeletonPipelineImpl.compiler();
    }

}
