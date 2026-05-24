package net.quepierts.thatskyinteractions.infra.animation.tween.interpolate;

import lombok.experimental.UtilityClass;
import org.joml.*;

@UtilityClass
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

}
