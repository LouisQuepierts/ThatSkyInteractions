package net.quepierts.thatskyinteractions.infra.animation.backend.sampler;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.WritableBuffer;
import net.quepierts.thatskyinteractions.infra.animation.backend.pipeline.AnimationContext;
import net.quepierts.thatskyinteractions.infra.animation.backend.source.AnimationSource;

public interface AnimationSampler {

     void sample(
            AnimationContext        context,
            WritableBuffer          target
    );

}
