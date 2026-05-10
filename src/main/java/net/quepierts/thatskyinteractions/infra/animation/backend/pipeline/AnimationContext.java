package net.quepierts.thatskyinteractions.infra.animation.backend.pipeline;

import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.AnimationBuffer;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.ChannelFormat;
import net.quepierts.thatskyinteractions.infra.animation.backend.sampler.AnimationSampler;
import net.quepierts.thatskyinteractions.infra.animation.backend.uniform.UniformReader;
import net.quepierts.thatskyinteractions.infra.animation.runtime.AnimationState;

public interface AnimationContext {

    float                   getProgress();

    ChannelFormat           getChannelFormat();

    AnimationState          getAnimationState();

    AnimationSampler<?>     getSampler(int location);

    AnimationFrameBuffer    getFrameBuffer(int location);

    AnimationBuffer         getParameterBuffer();

    UniformReader           getUniform();

    boolean                 getOperationMask(int index);

    boolean                 getSamplerMask(int channel);

}
