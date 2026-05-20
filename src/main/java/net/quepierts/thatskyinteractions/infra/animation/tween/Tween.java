package net.quepierts.thatskyinteractions.infra.animation.tween;

import lombok.experimental.UtilityClass;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.Consumer1f;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.Consumer2f;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.Consumer3f;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.TransformAccessor;
import net.quepierts.thatskyinteractions.infra.animation.tween.backend.TweenBackend;
import net.quepierts.thatskyinteractions.infra.animation.tween.interpolate.Interpolator;
import org.joml.Quaternionfc;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;

@UtilityClass
public class Tween {

    private static final TweenBackend BACKEND = TweenBackend.create();

    public static int to(
            @NonNull Consumer1f     target,
            float                   from,
            float                   to,
            float                   duration
    ) {
        return Tween.BACKEND.to(target, from, to, duration);
    }

    public static int to(
            @NonNull Consumer1f     target,
            @NonNull Interpolator lerp,
            float                   from,
            float                   to,
            float                   duration
    ) {
        return Tween.BACKEND.to(target, lerp, from, to, duration);
    }

    public static int to(
            @NonNull Consumer2f     target,
            @NonNull Vector2fc      from,
            @NonNull Vector2fc      to,
            float                   duration
    ) {
        return Tween.BACKEND.to(target, from, to, duration);
    }

    public static int to(
            @NonNull Consumer2f     target,
            @NonNull Interpolator   lerp,
            @NonNull Vector2fc      from,
            @NonNull Vector2fc      to,
            float                   duration
    ) {
        return Tween.BACKEND.to(target, lerp, from, to, duration);
    }

    public static int to(
            @NonNull Consumer3f     target,
            @NonNull Vector3fc      from,
            @NonNull Vector3fc      to,
            float                   duration
    ) {
        return Tween.BACKEND.to(target, from, to, duration);
    }

    public static int to(
            @NonNull Consumer3f     target,
            @NonNull Interpolator   lerp,
            @NonNull Vector3fc      from,
            @NonNull Vector3fc      to,
            float                   duration
    ) {
        return Tween.BACKEND.to(target, lerp, from, to, duration);
    }

    public static int translate(
            @NonNull TransformAccessor  transform,
            @NonNull Vector3fc          from,
            @NonNull Vector3fc          to,
            float                       duration
    ) {
        return Tween.BACKEND.translate(transform, from, to, duration);
    }

    public static int rotate(
            @NonNull TransformAccessor  transform,
            @NonNull Vector3fc          from,
            @NonNull Vector3fc          to,
            float                       duration
    ) {
        return Tween.BACKEND.rotate(transform, from, to, duration);
    }

    public static int rotate(
            @NonNull TransformAccessor  transform,
            @NonNull Quaternionfc       from,
            @NonNull Quaternionfc       to,
            float                       duration
    ) {
        return Tween.BACKEND.rotate(transform, from, to, duration);
    }

    public static int scale(
            @NonNull TransformAccessor  transform,
            @NonNull Vector3fc          from,
            @NonNull Vector3fc          to,
            float                       duration
    ) {
        return Tween.BACKEND.scale(transform, from, to, duration);
    }

    public static int wait(
            @NonNull Runnable           runnable,
            float                       duration
    ) {
        return Tween.BACKEND.wait(runnable, duration);
    }

    public static void terminate(int handle) {
        Tween.BACKEND.terminate(handle);
    }

}
