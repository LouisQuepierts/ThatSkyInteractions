package net.quepierts.thatskyinteractions.core.animation.sampler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.quepierts.veynir.backend.buffer.WritableBuffer;
import net.quepierts.veynir.backend.pipeline.AnimationContext;
import net.quepierts.veynir.backend.sampler.AnimationSampler;
import net.quepierts.veynir.backend.sampler.SamplingMode;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class WrappedSampler implements AnimationSampler {

    public static WrappedSampler wrap(
            final AnimationSampler  sampler,
            final SamplingMode      mode
    ) {

        if (sampler instanceof WrappedSampler wrapped) {
            return wrapped.mode == mode ? wrapped : new WrappedSampler(wrapped.delegate, mode);
        }

        return new WrappedSampler(sampler, mode);

    }

    private final AnimationSampler  delegate;
    private final SamplingMode      mode;

    @Override
    public void sample(
            final AnimationContext context,
            final WritableBuffer target,
            final SamplingMode mode,
            final float time
    ) {
        this.delegate.sample(context, target, this.mode, time);
    }
}
