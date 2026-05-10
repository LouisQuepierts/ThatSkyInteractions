package net.quepierts.thatskyinteractions.infra.animation.backend.pipeline;

import net.quepierts.thatskyinteractions.infra.animation.adapter.Consumer4f;
import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.AnimationBuffer;

public final class AnimationResultBuffer
        extends AnimationBuffer.Slice
        implements AnimationResultView {
    AnimationResultBuffer(AnimationBuffer buffer, int size) {
        super(buffer, 0, size);
    }

    @Override
    public void read(int channel, Consumer4f consumer) {
        this.buffer.readFloat(channel << 2, consumer);
    }

    @Override
    public void read(int channel, float[] out) {
        this.buffer.readFloat(channel << 2, 4, out);
    }
}
