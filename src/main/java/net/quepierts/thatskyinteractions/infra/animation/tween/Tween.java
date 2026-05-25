package net.quepierts.thatskyinteractions.infra.animation.tween;

import net.quepierts.thatskyinteractions.infra.animation.core.adapter.Consumer1f;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.TransformAccessor;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Ease;
import net.quepierts.thatskyinteractions.infra.animation.tween.interpolate.Interpolator;
import net.quepierts.thatskyinteractions.infra.animation.tween.interpolate.Interpolator1f;
import net.quepierts.thatskyinteractions.infra.animation.tween.backend.TweenScope;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public interface Tween {

    TweenScope GLOBAL = TweenScope.global();

    static TweenScope create() {
        return TweenScope.create();
    }

    static TweenHandle to(
            @NonNull Consumer1f                 target,
            float                               from,
            float                               to,
            float                               duration,

            @NonNull Interpolator1f interpolator,
            @NonNull Ease ease
    ) {
        return GLOBAL.to(target, from, to, duration, interpolator, ease);
    }

    static <T> TweenHandle to(
            @NonNull Consumer<T>                target,
            T                                   from,
            T                                   to,
            float                               duration,

            @NonNull Interpolator<T>            interpolator,
            @NonNull Ease                       ease
    ) {
        return GLOBAL.to(target, from, to, duration, interpolator, ease);
    }

    static TweenHandle translate(
            @NonNull TransformAccessor          transform,
            @NonNull Vector3fc                  from,
            @NonNull Vector3fc                  to,
            float                               duration,

            @NonNull Interpolator<Vector3fc>    interpolator,
            @NonNull Ease                       ease
    ) {
        return Tween.GLOBAL.translate(transform, from, to, duration, interpolator, ease);
    }

    static TweenHandle rotate(
            @NonNull TransformAccessor          transform,
            @NonNull Vector3fc                  from,
            @NonNull Vector3fc                  to,
            float                               duration,

            @NonNull Interpolator<Vector3fc>    interpolator,
            @NonNull Ease                       ease
    ) {
        return Tween.GLOBAL.rotate(transform, from, to, duration, interpolator, ease);
    }

    static TweenHandle rotate(
            @NonNull TransformAccessor          transform,
            @NonNull Quaternionfc               from,
            @NonNull Quaternionfc               to,
            float                               duration,

            @NonNull Interpolator<Quaternionfc> interpolator,
            @NonNull Ease                       ease
    ) {
        return Tween.GLOBAL.rotate(transform, from, to, duration, interpolator, ease);
    }

    static TweenHandle scale(
            @NonNull TransformAccessor          transform,
            @NonNull Vector3fc                  from,
            @NonNull Vector3fc                  to,
            float                               duration,

            @NonNull Interpolator<Vector3fc>    interpolator,
            @NonNull Ease                       ease
    ) {
        return Tween.GLOBAL.scale(transform, from, to, duration, interpolator, ease);
    }

    static TweenHandle wait(
            @NonNull Runnable                   runnable,
            float                               duration
    ) {
        return Tween.GLOBAL.wait(runnable, duration);
    }

}
