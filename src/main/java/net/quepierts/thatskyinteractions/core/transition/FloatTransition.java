package net.quepierts.thatskyinteractions.core.transition;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.Mth;
import net.quepierts.thatskyinteractions.core.property.FloatProperty;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenHandle;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Ease;
import net.quepierts.thatskyinteractions.infra.animation.tween.interpolate.Interpolator1f;
import net.quepierts.thatskyinteractions.infra.animation.tween.interpolate.Interpolators;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor
public final class FloatTransition {

    private final FloatProperty     value = new FloatProperty(0f);
    private final Ease              ease;
    private final Interpolator1f    interpolator;

    private final FloatProperty     duration;

    private       TweenHandle       handle;
    @Getter
    private       float             target;

    public FloatTransition(
            final @NonNull Ease     ease,
            final FloatProperty     duration
    ) {
        this.ease           = ease;
        this.interpolator   = Interpolators.FLOAT;
        this.duration       = duration;
    }

    public FloatTransition(
            final @NonNull Ease     ease,
            final float             duration
    ) {
        this.ease           = ease;
        this.interpolator   = Interpolators.FLOAT;
        this.duration       = new FloatProperty(duration);
    }

    public FloatTransition(
            final @NonNull Ease     ease,
            final Interpolator1f    interpolator,
            final float             duration
    ) {
        this.ease           = ease;
        this.interpolator   = interpolator;
        this.duration       = new FloatProperty(duration);
    }

    public void update(
            @NonNull final TweenScope   tween,
                     final float        target
    ) {
        if (target == this.target) {
            return;
        }
        this.target = target;

        if (this.handle != null) {
            this.handle.cancel();
        }

        float from  = this.value.get();

        this.handle = tween.to(
                this.value,
                from,
                target,
                this.duration.get() * Mth.abs(target - from),
                Interpolators.FLOAT,
                this.ease
        );
    }

    public void set(float target) {
        this.target = target;
        if (this.handle != null) {
            this.handle.cancel();
            this.handle = null;
        }
        this.value.set(target);
    }

    public float getValue() {
        return value.get();
    }

    public boolean isAnimating() {
        return handle != null && !handle.isFinished();
    }
}
