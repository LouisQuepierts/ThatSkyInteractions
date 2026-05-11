package net.quepierts.thatskyinteractions.infra.animation.runtime;

import lombok.Getter;
import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.AnimationBuffer;
import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.AttributeBuffer;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.ChannelFormat;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.ChannelLayout;

@Getter
public abstract class AnimationState {

    public float progress;
    public float lastProgress;

    private final AttributeBuffer channelAttribute;

    protected AnimationState(
            ChannelLayout channelLayout,
            ChannelFormat channelFormat
    ) {

        var attributes          = channelLayout.getChannelCount() * channelFormat.getAttributeSize();
        this.channelAttribute   = new AttributeBuffer(attributes);
    }

    public AnimationBuffer getParameterBuffer() {
        return null;
    }
}
