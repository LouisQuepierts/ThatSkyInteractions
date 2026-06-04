package net.quepierts.thatskyinteractions.feature.client.gui.component;

import lombok.experimental.UtilityClass;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Ease;

@UtilityClass
public class UiEases {

    public static final Ease BOUNCE = UiEases::bounce;

    public static float bounce(final float t) {
        return 4 * (t - t * t);
    }

}
