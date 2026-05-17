package net.quepierts.thatskyinteractions.infra.animation.backend.pipeline;

import net.quepierts.thatskyinteractions.infra.animation.backend.channel.ChannelFormat;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.ChannelLayout;
import net.quepierts.thatskyinteractions.infra.animation.backend.sampler.AnimationSampler;
import net.quepierts.thatskyinteractions.infra.animation.backend.uniform.UniformBuffer;
import net.quepierts.thatskyinteractions.infra.animation.core.AnimationState;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.AnimationOutput;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.PipelineInputProvider;
import net.quepierts.thatskyinteractions.infra.util.LocationLookup;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface AnimationPipeline {
    String ORIGINAL_SAMPLER = "Pipeline.OriginSampler";
    String OUTPUT_BUFFER = "Pipeline.ResultBuffer";

    @Contract(" -> new")
    static DefaultAnimationPipelineImpl.@NonNull Compiler compiler() {
        return DefaultAnimationPipelineImpl.compiler();
    }

    void submit(
            @NonNull AnimationState state,
            @NonNull AnimationOutput output
    );

    void submit(
            @NonNull AnimationState state,
            @Nullable PipelineInputProvider input,
            @NonNull AnimationOutput output
    );

    void bindSource(
            String name,
            AnimationSampler sampler
    );

    void bindSource(
            int location,
            AnimationSampler sampler
    );

    void bindUbo(
            String name,
            UniformBuffer buffer
    );

    void bindUbo(
            int location,
            UniformBuffer buffer
    );

    ChannelFormat getChannelFormat();

    ChannelLayout getChannelLayout();

    LocationLookup getBufferLookup();

    LocationLookup getSamplerLookup();

    LocationLookup getUboLookup();

    UniformBuffer getUniform();
}
