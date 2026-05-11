package net.quepierts.thatskyinteractions.infra.animation.backend.pipeline;

import net.quepierts.thatskyinteractions.infra.animation.adapter.PipelineInputProvider;
import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.AnimationBuffer;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.ChannelFormat;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.ChannelLayout;
import net.quepierts.thatskyinteractions.infra.animation.backend.sampler.AnimationSampler;
import net.quepierts.thatskyinteractions.infra.animation.backend.uniform.UniformReader;
import net.quepierts.thatskyinteractions.infra.animation.runtime.AnimationState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface AnimationContext {

    float                               getProgress();

    @NonNull    ChannelLayout           getChannelLayout();

    @NonNull    ChannelFormat           getChannelFormat();

    @NonNull    AnimationState          getAnimationState();

    @NonNull    AnimationSampler        getSampler(int location);

    @NonNull    AnimationFrameBuffer    getFrameBuffer(int location);

    @NonNull    AnimationBuffer         getParameterBuffer();

    @NonNull    UniformReader           getUniform();

    @Nullable   PipelineInputProvider   getInputProvider();

    boolean                             getOperationMask(int index);

    boolean                             getSamplerMask(int channel);

}
