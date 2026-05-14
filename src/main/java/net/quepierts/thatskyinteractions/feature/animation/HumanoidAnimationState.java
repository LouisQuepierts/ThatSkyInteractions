package net.quepierts.thatskyinteractions.feature.animation;

import net.quepierts.thatskyinteractions.infra.animation.backend.channel.ChannelFormat;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.DefaultChannelFormats;
import net.quepierts.thatskyinteractions.infra.animation.runtime.AnimationState;

public final class HumanoidAnimationState extends AnimationState {

    public static HumanoidAnimationState _default() {
        return new HumanoidAnimationState(DefaultChannelFormats.TIMELINE);
    }

    public HumanoidAnimationState(ChannelFormat channelFormat) {
        super(DefaultMinecraftChannelLayout.HUMANOID, channelFormat);
    }

}
