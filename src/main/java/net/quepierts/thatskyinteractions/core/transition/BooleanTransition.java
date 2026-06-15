package net.quepierts.thatskyinteractions.core.transition;

import lombok.RequiredArgsConstructor;
import net.minecraft.util.Mth;
import net.quepierts.thatskyinteractions.core.property.FloatProperty;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenHandle;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Ease;
import net.quepierts.thatskyinteractions.infra.animation.tween.interpolate.Interpolators;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor
public final class BooleanTransition {

    private final FloatProperty     value = new FloatProperty(0f);
    private final Ease              ease;

    private final FloatProperty     duration;

    private       TweenHandle       handle;
    private       boolean           target;

    public BooleanTransition(
            final @NonNull Ease ease,
            final float duration
    ) {
        this.ease = ease;
        this.duration = new FloatProperty(duration);
    }


    public void update(
            final @NonNull TweenScope   tween,
                     final boolean      target
    ) {
        if (target == this.target) {
            return;
        }
        this.target = target;

        if (this.handle != null) {
            this.handle.cancel();
        }

        float from  = this.value.get();
        float to    = this.target ? 1f : 0f;

        this.handle = tween.to(
                this.value,
                from,
                to,
                this.duration.get() * Mth.abs(to - from),
                Interpolators.FLOAT,
                this.ease
        );
    }

    public void set(boolean target) {
        this.target = target;
        if (this.handle != null) {
            this.handle.cancel();
            this.handle = null;
        }
        this.value.set(target ? 1f : 0f);
    }

    public float getValue() {
        return value.get();
    }

    public boolean getTarget() {
        return target;
    }

    public boolean isAnimating() {
        return handle != null && !handle.isFinished();
    }
}
