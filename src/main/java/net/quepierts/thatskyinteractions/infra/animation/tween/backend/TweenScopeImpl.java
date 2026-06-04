package net.quepierts.thatskyinteractions.infra.animation.tween.backend;

import net.quepierts.veynir.core.adapter.Consumer1f;
import net.quepierts.veynir.core.adapter.TransformAccessor;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import net.quepierts.thatskyinteractions.infra.animation.tween.backend.task.*;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Ease;
import net.quepierts.thatskyinteractions.infra.animation.tween.interpolate.Interpolator;
import net.quepierts.thatskyinteractions.infra.animation.tween.interpolate.Interpolator1f;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenHandle;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public final class TweenScopeImpl
        implements TweenScope, TweenTickHandler {

    private final TweenScheduler scheduler = new TweenScheduler();

    public TweenScopeImpl() {}

    @Override
    public TweenHandle to(@NonNull final Consumer1f target, final float from, final float to, final float duration, @NonNull final Interpolator1f interpolator, @NonNull final Ease ease) {
        final var task  = new TweenTask1f(
                        duration,
                        from,
                        to,
                        ease,
                        interpolator,
                        target
        );

        this.scheduler  .submit(task);

        return DefaultTweenHandle.of(task);
    }

    @Override
    public <T> TweenHandle to(@NonNull final Consumer<T> target, final T from, final T to, final float duration, @NonNull final Interpolator<T> interpolator, @NonNull final Ease ease) {
        final var task  = new TweenTask<>(
                        duration,
                        from,
                        to,
                        ease,
                        interpolator,
                        target
        );

        this.scheduler  .submit(task);

        return DefaultTweenHandle.of(task);
    }

    @Override
    public TweenHandle translate(@NonNull final TransformAccessor transform, @NonNull final Vector3fc from, @NonNull final Vector3fc to, final float duration, @NonNull final Interpolator<Vector3fc> interpolator, @NonNull final Ease ease) {
        final var task  = new TweenTask<>(
                        duration,
                        from,
                        to,
                        ease,
                        interpolator,
                        vec -> transform.setPosition(vec.x(), vec.y(), vec.z())
        );

        this.scheduler  .submit(task);

        return DefaultTweenHandle.of(task);
    }

    @Override
    public TweenHandle rotate(@NonNull final TransformAccessor transform, @NonNull final Vector3fc from, @NonNull final Vector3fc to, final float duration, @NonNull final Interpolator<Vector3fc> interpolator, @NonNull final Ease ease) {
        final var task  = new TweenTask<>(
                        duration,
                        from,
                        to,
                        ease,
                        interpolator,
                        vec -> transform.setEulerAngle(vec.x(), vec.y(), vec.z())
        );

        this.scheduler  .submit(task);

        return DefaultTweenHandle.of(task);
    }

    @Override
    public TweenHandle rotate(@NonNull final TransformAccessor transform, @NonNull final Quaternionfc from, @NonNull final Quaternionfc to, final float duration, @NonNull final Interpolator<Quaternionfc> interpolator, @NonNull final Ease ease) {
        final var task  = new TweenTask<>(
                        duration,
                        from,
                        to,
                        ease,
                        interpolator,
                        quat -> transform.setQuaternion(quat.x(), quat.y(), quat.z(), quat.w())
        );

        this.scheduler  .submit(task);

        return DefaultTweenHandle.of(task);
    }

    @Override
    public TweenHandle scale(@NonNull final TransformAccessor transform, @NonNull final Vector3fc from, @NonNull final Vector3fc to, final float duration, @NonNull final Interpolator<Vector3fc> interpolator, @NonNull final Ease ease) {
        final var task  = new TweenTask<>(
                        duration,
                        from,
                        to,
                        ease,
                        interpolator,
                        vec -> transform.setScale(vec.x(), vec.y(), vec.z())
        );

        this.scheduler  .submit(task);

        return DefaultTweenHandle.of(task);
    }

    @Override
    public TweenHandle wait(@NonNull final Runnable runnable, final float duration) {
        final var task  = new WaitTask(
                        duration,
                        runnable
        );

        this.scheduler  .submit(task);

        return DefaultTweenHandle.of(task);
    }

    @Override
    public boolean isRunning() {
        return this.scheduler.isRunning();
    }

    @Override
    public void tick(final float delta) {
        this.scheduler.update(delta);
    }
}
