package net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline;

import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.SkeletonLayout;
import net.quepierts.thatskyinteractions.infra.animation.backend.uniform.UniformBuffer;
import net.quepierts.thatskyinteractions.infra.animation.core.SkeletonState;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.AnimationOutput;
import net.quepierts.thatskyinteractions.infra.util.LocationLookup;
import org.jspecify.annotations.NonNull;

public interface SkeletonPipeline {

    String INPUT_BUFFER = "Pipeline.OriginBuffer";
    String OUTPUT_BUFFER = "Pipeline.ResultBuffer";

    static DefaultSkeletonPipelineImpl.Compiler compiler() {
        return DefaultSkeletonPipelineImpl.compiler();
    }

    void submit(@NonNull SkeletonState state);

    void bindUbo(
            String name,
            UniformBuffer buffer
    );

    void bindUbo(
            int location,
            UniformBuffer buffer
    );

    void bindTarget(
            String name,
            SkeletonOutput target
    );

    void bindTarget(
            int location,
            SkeletonOutput target
    );

    SkeletonLayout getLayout();

    LocationLookup getBufferLookup();

    LocationLookup getUboLookup();

    UniformBuffer getUniform();

    AnimationOutput getAdapter();

}
