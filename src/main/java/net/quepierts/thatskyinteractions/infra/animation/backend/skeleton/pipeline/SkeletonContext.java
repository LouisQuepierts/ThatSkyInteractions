package net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline;

import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.SkeletonLayout;
import net.quepierts.thatskyinteractions.infra.animation.backend.uniform.UniformReader;
import net.quepierts.thatskyinteractions.infra.animation.core.SkeletonState;
import org.jspecify.annotations.NonNull;

public interface SkeletonContext {

    @NonNull SkeletonLayout         getLayout();

    @NonNull SkeletonState          getState();

    @NonNull SkeletonPoseProvider   getProvider(int location);

    @NonNull SkeletonPoseBuffer     getPoseBuffer(int location);

    @NonNull UniformReader          getUniform();

    @NonNull UniformReader          getUniformBuffer(int location);

}
