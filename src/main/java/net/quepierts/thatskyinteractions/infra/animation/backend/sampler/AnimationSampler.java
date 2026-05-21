package net.quepierts.thatskyinteractions.infra.animation.backend.sampler;

import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.WritableBuffer;
import net.quepierts.thatskyinteractions.infra.animation.backend.pipeline.AnimationContext;

public interface AnimationSampler {

     void sample(
            AnimationContext        context,
            WritableBuffer          target
     );

}
