package net.quepierts.thatskyinteractions.feature.utils;

import lombok.experimental.UtilityClass;
import net.minecraft.util.Mth;
import net.quepierts.thatskyinteractions.infra.animation.tween.interpolate.Interpolator1f;

@UtilityClass
public class Interpolators {

    public static final Interpolator1f DEGREE = (from, to, progress) -> {
        final var difference = Mth.degreesDifference(from, to);
        return Mth.approach(from, from + difference, difference * progress);
    };

}
