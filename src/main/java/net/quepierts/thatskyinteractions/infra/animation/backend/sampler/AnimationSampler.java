package net.quepierts.thatskyinteractions.infra.animation.backend.sampler;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.WritableBuffer;
import net.quepierts.thatskyinteractions.infra.animation.backend.pipeline.AnimationContext;
import net.quepierts.thatskyinteractions.infra.animation.backend.source.AnimationSource;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AnimationSampler<T extends AnimationSource> {

    @Getter
    private final T source;

    public abstract void sample(
            AnimationContext        context,
            WritableBuffer          target
    );

}
