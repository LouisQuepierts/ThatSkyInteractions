package net.quepierts.thatskyinteractions.infra.animation.tween.backend;

import net.quepierts.thatskyinteractions.infra.animation.core.adapter.Consumer1f;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.Consumer2f;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.Consumer3f;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.TransformAccessor;
import net.quepierts.thatskyinteractions.infra.animation.tween.interpolate.Interpolator;
import org.joml.Quaternionfc;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;

public interface TweenBackend {
    static TweenBackend create() {
        return new TweenBackendImpl();
    }

    int to(
            @NonNull Consumer1f         target,
            float                       from,
            float                       to,
            float                       duration
    );

    int to(
            @NonNull Consumer1f         target,
            @NonNull Interpolator       lerp,
            float                       from,
            float                       to,
            float                       duration
    );

    int to(
            @NonNull Consumer2f         target,
            @NonNull Vector2fc          from,
            @NonNull Vector2fc          to,
            float                       duration
    );

    int to(
            @NonNull Consumer2f         target,
            @NonNull Interpolator       lerp,
            @NonNull Vector2fc          from,
            @NonNull Vector2fc          to,
            float                       duration
    );

    int to(
            @NonNull Consumer3f         target,
            @NonNull Vector3fc          from,
            @NonNull Vector3fc          to,
            float                       duration
    );

    int to(
            @NonNull Consumer3f         target,
            @NonNull Interpolator       lerp,
            @NonNull Vector3fc          from,
            @NonNull Vector3fc          to,
            float                       duration
    );

    int translate(
            @NonNull TransformAccessor transform,
            @NonNull Vector3fc          from,
            @NonNull Vector3fc          to,
            float                       duration
    );

    int rotate(
            @NonNull TransformAccessor  transform,
            @NonNull Vector3fc          from,
            @NonNull Vector3fc          to,
            float                       duration
    );

    int rotate(
            @NonNull TransformAccessor  transform,
            @NonNull Quaternionfc       from,
            @NonNull Quaternionfc       to,
            float                       duration
    );

    int scale(
            @NonNull TransformAccessor  transform,
            @NonNull Vector3fc          from,
            @NonNull Vector3fc          to,
            float                       duration
    );

    int wait(
            @NonNull Runnable           runnable,
            float                       duration
    );


    void terminate(int handle);
}
