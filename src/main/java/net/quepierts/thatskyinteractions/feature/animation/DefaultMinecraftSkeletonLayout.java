package net.quepierts.thatskyinteractions.feature.animation;

import lombok.experimental.UtilityClass;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.SkeletonLayout;

@UtilityClass
public class DefaultMinecraftSkeletonLayout {

    public static final SkeletonLayout HUMANOID = SkeletonLayout.builder()
            .bone("body")
            .bone("head")
            .bone("left_arm")
            .bone("right_arm")
            .bone("left_leg")
            .bone("right_leg")

            .parent("body", "root")
            .parent("head", "body")
            .parent("left_arm", "body")
            .parent("right_arm", "body")
            .parent("left_leg", "body")
            .parent("right_leg", "body")

            .build();

}
