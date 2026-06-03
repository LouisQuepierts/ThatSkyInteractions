package net.quepierts.thatskyinteractions.infra.animation.tween.interpolate;

import lombok.experimental.UtilityClass;
import org.joml.*;

@UtilityClass
@SuppressWarnings("unused")
public class Interpolators {

    public static final Interpolator1f FLOAT = (from, to, progress) -> from + progress * (to - from);

    public static final Interpolator<Vector2fc> FLOAT2 = (from, to, progress) -> {
        Vector2f vector = new Vector2f();
        from.lerp(to, progress, vector);
        return vector;
    };

    public static final Interpolator<Vector3fc> FLOAT3 = (from, to, progress) -> {
        Vector3f vector = new Vector3f();
        from.lerp(to, progress, vector);
        return vector;
    };

    public static final Interpolator<Vector4fc> FLOAT4 = (from, to, progress) -> {
        Vector4f vector = new Vector4f();
        from.lerp(to, progress, vector);
        return vector;
    };

    public static final Interpolator<Quaternionfc> QUATERNION = (from, to, progress) -> {
        Quaternionf quaternion = new Quaternionf();
        from.slerp(to, progress, quaternion);
        return quaternion;
    };

    public static final Interpolator1i INT = (from, to, progress) -> (int) (from + progress * (to - from));

    public static final Interpolator1i PACKED_COLOR = (from, to, progress) -> {
        int a0 = (from >> 24) & 0xFF;
        int r0 = (from >> 16) & 0xFF;
        int g0 = (from >> 8) & 0xFF;
        int b0 = from & 0xFF;
        int a1 = (to >> 24) & 0xFF;
        int r1 = (to >> 16) & 0xFF;
        int g1 = (to >> 8) & 0xFF;
        int b1 = to & 0xFF;
        return (int) (a0 + progress * (a1 - a0)) << 24 |
                (int) (r0 + progress * (r1 - r0)) << 16 |
                (int) (g0 + progress * (g1 - g0)) << 8 |
                (int) (b0 + progress * (b1 - b0));
    };

}
