package net.quepierts.thatskyinteractions.infra.animation.backend.source;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.infra.animation.backend.pipeline.AnimationPipeline;
import net.quepierts.thatskyinteractions.infra.animation.backend.sampler.AnimationSampler;
import net.quepierts.thatskyinteractions.infra.util.LocationLookup;
import org.jspecify.annotations.NonNull;

@Getter
@RequiredArgsConstructor
public abstract class AnimationSource {

    private final LocationLookup channels;

    public abstract @NonNull AnimationSampler link(@NonNull AnimationPipeline pipeline);

}
