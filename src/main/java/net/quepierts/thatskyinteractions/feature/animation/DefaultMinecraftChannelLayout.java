package net.quepierts.thatskyinteractions.feature.animation;

import lombok.experimental.UtilityClass;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.ChannelLayout;

@UtilityClass
public class DefaultMinecraftChannelLayout {

    public static final ChannelLayout HUMANOID = ChannelLayout.builder()
            .transform("root")
            .transform("body")
            .transform("head")
            .transform("left_arm")
            .transform("right_arm")
            .transform("left_leg")
            .transform("right_leg")
            .build();

}
