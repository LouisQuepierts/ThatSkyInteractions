package net.quepierts.thatskyinteractions.feature.animation;

import net.quepierts.thatskyinteractions.infra.animation.backend.channel.ChannelFormat;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.DefaultChannelFormats;
import net.quepierts.thatskyinteractions.infra.animation.core.AnimationState;

public final class HumanoidAnimationState extends AnimationState {

    private float start;

    public static HumanoidAnimationState _default() {
        return new HumanoidAnimationState(DefaultChannelFormats.TIMELINE);
    }

    public HumanoidAnimationState(ChannelFormat channelFormat) {
        super(DefaultMinecraftChannelLayout.HUMANOID, channelFormat);
    }

    public void update(float current) {
        // for test
        if (current - this.start > 60.0f) {
            this.start = current;
        }
        this.progress   = (current - this.start) * 0.05f;
    }

    public void start(float current) {
        this.start      = current;
    }
}
