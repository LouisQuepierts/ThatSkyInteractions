package net.quepierts.thatskyinteractions.infra.animation.backend.pipeline;

import net.quepierts.thatskyinteractions.infra.animation.backend.channel.ChannelFormat;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.ChannelLayout;
import net.quepierts.thatskyinteractions.infra.animation.backend.execution.ExecutionReflection;
import net.quepierts.thatskyinteractions.infra.animation.backend.execution.ExecutionState;
import net.quepierts.thatskyinteractions.infra.animation.backend.sampler.AnimationSampler;
import net.quepierts.thatskyinteractions.infra.animation.backend.uniform.UniformBuffer;
import net.quepierts.thatskyinteractions.infra.animation.core.AnimationState;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.AnimationOutput;
import net.quepierts.thatskyinteractions.infra.util.LocationLookup;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

public interface AnimationPipeline {
    String ORIGINAL_SAMPLER = "Sampler#Origin";
    String OUTPUT_BUFFER = "Buffer#Result";

    @Contract(" -> new")
    static DefaultAnimationPipelineImpl.@NonNull Compiler compiler() {
        return DefaultAnimationPipelineImpl.compiler();
    }

    void submit(
            @NonNull AnimationState state
    );

    default void submit(
            @NonNull AnimationState state,
            @NonNull AnimationOutput output
    ) {
        this.bindTarget(0, output);
        this.submit(state);
        this.bindTarget(0, null);
    }

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

    void bindTarget(
            String name,
            AnimationOutput target
    );

    void bindTarget(
            int location,
            AnimationOutput target
    );


    ChannelFormat getChannelFormat();

    ChannelLayout getChannelLayout();

    UniformBuffer getUniform();

    ExecutionReflection getReflection();

    ExecutionState getExecutionState();
}
