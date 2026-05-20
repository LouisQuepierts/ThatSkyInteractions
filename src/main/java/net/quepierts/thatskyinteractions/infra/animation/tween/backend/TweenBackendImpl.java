package net.quepierts.thatskyinteractions.infra.animation.tween.backend;

import net.quepierts.thatskyinteractions.infra.Services;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.Consumer1f;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.Consumer2f;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.Consumer3f;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.TransformAccessor;
import net.quepierts.thatskyinteractions.infra.animation.tween.interpolate.Interpolator;
import org.joml.Quaternionfc;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;

public final class TweenBackendImpl
        implements TweenBackend, TweenTickHandler {

    TweenBackendImpl() {
        Services.load(TweenTickRegistrar.class).register(this);
    }

    @Override
    public int to(
            @NonNull Consumer1f     target,
            float                   from,
            float                   to,
            float                   duration
    ) {
        return -1;
    }

    @Override
    public int to(
            @NonNull Consumer1f     target,
            @NonNull Interpolator   lerp,
            float                   from,
            float                   to,
            float                   duration
    ) {

        return -1;
    }

    @Override
    public int to(
            @NonNull Consumer2f     target,
            @NonNull Vector2fc      from,
            @NonNull Vector2fc      to,
            float                   duration
    ) {
        return -1;
    }

    @Override
    public int to(
            @NonNull Consumer2f     target,
            @NonNull Interpolator   lerp,
            @NonNull Vector2fc      from,
            @NonNull Vector2fc      to,
            float                   duration
    ) {
        return -1;
    }

    @Override
    public int to(
            @NonNull Consumer3f     target,
            @NonNull Vector3fc      from,
            @NonNull Vector3fc      to,
            float                   duration
    ) {
        return -1;
    }

    @Override
    public int to(
            @NonNull Consumer3f     target,
            @NonNull Interpolator   lerp,
            @NonNull Vector3fc      from,
            @NonNull Vector3fc      to,
            float                   duration
    ) {
        return -1;
    }

    @Override
    public int translate(
            @NonNull TransformAccessor  transform,
            @NonNull Vector3fc          from,
            @NonNull Vector3fc          to,
            float                       duration
    ) {

        return -1;
    }

    @Override
    public int rotate(
            @NonNull TransformAccessor  transform,
            @NonNull Vector3fc          from,
            @NonNull Vector3fc          to,
            float                       duration
    ) {

        return -1;
    }

    @Override
    public int rotate(
            @NonNull TransformAccessor  transform,
            @NonNull Quaternionfc       from,
            @NonNull Quaternionfc       to,
            float                       duration
    ) {

        return -1;
    }

    @Override
    public int scale(
            @NonNull TransformAccessor  transform,
            @NonNull Vector3fc          from,
            @NonNull Vector3fc          to,
            float                       duration
    ) {

        return -1;
    }

    @Override
    public int wait(
            @NonNull Runnable           runnable,
            float                       duration
    ) {

        return -1;
    }

    @Override
    public void terminate(int handle) {

    }

    @Override
    public void tick(final float delta) {
    }
}
