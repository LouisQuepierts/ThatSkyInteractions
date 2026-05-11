package net.quepierts.thatskyinteractions.infra.animation.backend.source;

import lombok.Getter;
import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.AnimationBuffer;
import net.quepierts.thatskyinteractions.infra.animation.backend.pipeline.AnimationPipeline;
import net.quepierts.thatskyinteractions.infra.animation.backend.sampler.AnimationBufferSampler;
import net.quepierts.thatskyinteractions.infra.util.LocationLookup;
import org.jspecify.annotations.NonNull;

@Getter
public final class AnimationBufferSource extends AnimationSource {

    private final AnimationBuffer.Slice buffer;

    public AnimationBufferSource(LocationLookup channels, AnimationBuffer.Slice buffer) {
        super(channels);
        this.buffer = buffer;
    }

    @Override
    public @NonNull AnimationBufferSampler link(@NonNull AnimationPipeline pipeline) {
        return AnimationBufferSampler.of(this, pipeline.getChannelLayout());
    }
}
