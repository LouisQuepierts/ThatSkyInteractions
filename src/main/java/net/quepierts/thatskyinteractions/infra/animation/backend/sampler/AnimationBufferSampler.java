package net.quepierts.thatskyinteractions.infra.animation.backend.sampler;

import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.WritableBuffer;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.ChannelLayout;
import net.quepierts.thatskyinteractions.infra.animation.backend.pipeline.AnimationContext;
import net.quepierts.thatskyinteractions.infra.animation.backend.source.AnimationBufferSource;
import org.jspecify.annotations.NonNull;

public final class AnimationBufferSampler extends AnimationSampler<AnimationBufferSource> {

    public static AnimationBufferSampler of(
            @NonNull AnimationBufferSource source,
            @NonNull ChannelLayout layout
    ) {

        var same    = source.getChannels() == layout.getLookup();

        if (!same && (source.getBuffer().getSize() < layout.getChannelCount() << 2)) {
            throw new IllegalArgumentException("Buffer size is not enough");
        }

        return new AnimationBufferSampler(source);
    }

    private AnimationBufferSampler(AnimationBufferSource source) {
        super(source);
    }

    @Override
    public void sample(AnimationContext context, WritableBuffer target) {
        var layout      = context.getChannelLayout();
        var source      = this.getSource().getBuffer();
        var raw         = source.getBuffer().getBuffer();

        for (int i = 0; i < layout.getChannelCount(); i++) {
            if (!context.getSamplerMask(i)) {
                continue;
            }

            var addr    = i << 2;
            var base    = addr + source.getOffset();

            target.write(
                    addr,
                    raw[base],
                    raw[base + 1],
                    raw[base + 2],
                    raw[base + 3]
            );
        }
    }
}
