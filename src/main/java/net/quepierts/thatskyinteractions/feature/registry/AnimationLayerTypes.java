package net.quepierts.thatskyinteractions.feature.registry;

import lombok.experimental.UtilityClass;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerBone;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerMask;
import net.quepierts.thatskyinteractions.feature.registry.builer.AnimationLayerBuilder;
import net.quepierts.thatskyinteractions.feature.registry.entry.AnimationLayerEntry;

@UtilityClass
public class AnimationLayerTypes {

    public static final AnimationLayerEntry DEFAULT
            = type("default")
            .mask(PlayerMask.all())
            .register();

    public static final AnimationLayerEntry LEFT_ARM
            = type("left_arm")
            .priority(-100)
            .exclusive(true)
            .mask(PlayerBone.LEFT_ARM)
            .register();

    public static final AnimationLayerEntry RIGHT_ARM
            = type("right_arm")
            .priority(-100)
            .exclusive(true)
            .mask(PlayerBone.RIGHT_ARM)
            .register();

    public static final AnimationLayerEntry UPPER
            = type("upper")
            .priority(-100)
            .exclusive(true)
            .mask(PlayerBone.HEAD, PlayerBone.LEFT_ARM, PlayerBone.RIGHT_ARM)
            .register();

    public static final AnimationLayerEntry LOWER
            = type("lower")
            .priority(-100)
            .exclusive(true)
            .mask(PlayerBone.BODY, PlayerBone.LEFT_LEG, PlayerBone.RIGHT_LEG)
            .register();

    public static void register() { }

    private static AnimationLayerBuilder type(String name) {
        return ThatSkyInteractions.REGISTRUM.entry(
                name,
                callback -> new AnimationLayerBuilder(
                        ThatSkyInteractions.REGISTRUM,
                        ThatSkyInteractions.REGISTRUM,
                        name,
                        callback
                )
        );
    }

}
