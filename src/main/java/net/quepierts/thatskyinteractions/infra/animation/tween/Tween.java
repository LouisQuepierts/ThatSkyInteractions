package net.quepierts.thatskyinteractions.infra.animation.tween;

import lombok.experimental.UtilityClass;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.Consumer1f;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.TransformAccessor;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Ease;
import net.quepierts.thatskyinteractions.infra.animation.tween.interpolate.Interpolator;
import net.quepierts.thatskyinteractions.infra.animation.tween.interpolate.Interpolator1f;
import net.quepierts.thatskyinteractions.infra.animation.tween.backend.TweenBackend;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public interface Tween {

    TweenBackend BACKEND = TweenBackend.create();

    static TweenHandle to(
            @NonNull Consumer1f                 target,
            float                               from,
            float                               to,
            float                               duration,

            @NonNull Interpolator1f interpolator,
            @NonNull Ease ease
    ) {
        return BACKEND.to(target, from, to, duration, interpolator, ease);
    }

    static <T> TweenHandle to(
            @NonNull Consumer<T>                target,
            T                                   from,
            T                                   to,
            float                               duration,

            @NonNull Interpolator<T>            interpolator,
            @NonNull Ease                       ease
    ) {
        return BACKEND.to(target, from, to, duration, interpolator, ease);
    }

    static TweenHandle translate(
            @NonNull TransformAccessor          transform,
            @NonNull Vector3fc                  from,
            @NonNull Vector3fc                  to,
            float                               duration,

            @NonNull Interpolator<Vector3fc>    interpolator,
            @NonNull Ease                       ease
    ) {
        return Tween.BACKEND.translate(transform, from, to, duration, interpolator, ease);
    }

    static TweenHandle rotate(
            @NonNull TransformAccessor          transform,
            @NonNull Vector3fc                  from,
            @NonNull Vector3fc                  to,
            float                               duration,

            @NonNull Interpolator<Vector3fc>    interpolator,
            @NonNull Ease                       ease
    ) {
        return Tween.BACKEND.rotate(transform, from, to, duration, interpolator, ease);
    }

    static TweenHandle rotate(
            @NonNull TransformAccessor          transform,
            @NonNull Quaternionfc               from,
            @NonNull Quaternionfc               to,
            float                               duration,

            @NonNull Interpolator<Quaternionfc> interpolator,
            @NonNull Ease                       ease
    ) {
        return Tween.BACKEND.rotate(transform, from, to, duration, interpolator, ease);
    }

    static TweenHandle scale(
            @NonNull TransformAccessor          transform,
            @NonNull Vector3fc                  from,
            @NonNull Vector3fc                  to,
            float                               duration,

            @NonNull Interpolator<Vector3fc>    interpolator,
            @NonNull Ease                       ease
    ) {
        return Tween.BACKEND.scale(transform, from, to, duration, interpolator, ease);
    }

    static TweenHandle wait(
            @NonNull Runnable                   runnable,
            float                               duration
    ) {
        return Tween.BACKEND.wait(runnable, duration);
    }

}
