package net.quepierts.thatskyinteractions.infra.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Mth {

    public static float lerp(float delta, float start, float end) {
        return start + delta * (end - start);
    }

    public static float catmullrom(float delta, float start, float left, float right, float end) {
        return lerp(delta, lerp(delta, start, left), lerp(delta, right, end));
    }

}
