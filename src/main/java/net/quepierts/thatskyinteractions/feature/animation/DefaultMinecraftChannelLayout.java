package net.quepierts.thatskyinteractions.feature.animation;

import lombok.experimental.UtilityClass;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.ChannelLayout;

@UtilityClass
public class DefaultMinecraftChannelLayout {

    public static final ChannelLayout HUMANOID = DefaultMinecraftSkeletonLayout.HUMANOID
                                                .toChannelLayout();

}
