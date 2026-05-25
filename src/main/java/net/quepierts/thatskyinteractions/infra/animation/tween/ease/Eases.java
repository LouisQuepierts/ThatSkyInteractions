package net.quepierts.thatskyinteractions.infra.animation.tween.ease;

import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings("unused")
public class Eases {

    public static final Ease LINEAR = t -> t;

    public static final Ease QUAD_IN = t -> t * t;

    public static final Ease QUAD_OUT = t -> t * (2 - t);

    public static final Ease CUBIC_IN = t -> t * t * t;

    public static final Ease CUBIC_OUT = t -> --t * t * t + 1;

}
