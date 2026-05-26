package net.quepierts.thatskyinteractions.infra.animation.backend.sampler;

import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.WritableBuffer;
import net.quepierts.thatskyinteractions.infra.animation.backend.pipeline.AnimationContext;

public interface AnimationSampler {

     void sample(
            final AnimationContext      context,
            final WritableBuffer        target,
            final SamplingMode          mode,
            final float                 time
     );

}
