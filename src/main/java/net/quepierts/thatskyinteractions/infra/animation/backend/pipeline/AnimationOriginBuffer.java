package net.quepierts.thatskyinteractions.infra.animation.backend.pipeline;

import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.AnimationBuffer;

public class AnimationOriginBuffer
        extends AnimationBuffer.Slice
        implements AnimationOriginView {
    AnimationOriginBuffer(AnimationBuffer buffer, int offset, int size) {
        super(buffer, offset, size);
    }
}
