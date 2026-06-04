package net.quepierts.thatskyinteractions.core.animation;

import lombok.experimental.UtilityClass;
import net.quepierts.veynir.backend.channel.ChannelLayout;

@UtilityClass
public class DefaultMinecraftChannelLayout {

    public static final ChannelLayout HUMANOID = DefaultMinecraftSkeletonLayout.HUMANOID
                                                .toChannelLayout();

}
