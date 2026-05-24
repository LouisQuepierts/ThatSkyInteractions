package net.quepierts.thatskyinteractions.infra.animation.tween.ease;

import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings("unused")
public class Eases {

    public static final Ease LINEAR = t -> t;

    public static final Ease CUBIC_IN = t -> t * t * t;

}
