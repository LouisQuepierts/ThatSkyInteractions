package net.quepierts.thatskyinteractions.infra.animation.backend.sampler;

import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.WritableBuffer;
import net.quepierts.thatskyinteractions.infra.animation.backend.pipeline.AnimationContext;

public final class OriginSampler implements AnimationSampler {
    @Override
    public void sample(AnimationContext context, WritableBuffer target) {
        var input       = context.getInputProvider();

        if (input == null) {
            return;
        }

        var layout      = context.getChannelLayout();
        for (int i = 0; i < layout.getChannelCount(); i++) {
            if (!context.getSamplerMask(i)) {
                continue;
            }

            input       .fill(i, i << 2, target);
        }
    }
}
