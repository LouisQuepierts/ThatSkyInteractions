package net.quepierts.thatskyinteractions.infra.animation.backend.channel;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DefaultChannelLayouts {

    public static final ChannelLayout HUMANOID = ChannelLayout.builder()
            .add("Hips")
            .add("Spine")
            .add("Spine1")
            .add("Neck")
            .add("Head")
            .add("LeftShoulder")
            .add("LeftArm")
            .add("LeftForeArm")
            .build();

}
