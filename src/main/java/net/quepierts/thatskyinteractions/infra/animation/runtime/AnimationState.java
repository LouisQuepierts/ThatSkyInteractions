package net.quepierts.thatskyinteractions.infra.animation.runtime;

import lombok.Getter;
import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.AnimationBuffer;
import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.AttributeBuffer;

@Getter
public abstract class AnimationState {

    public float progress;
    public float lastProgress;

    public AttributeBuffer getChannelAttribute() {
        return null;
    }

    public AnimationBuffer getParameterBuffer() {
        return null;
    }
}
