package net.quepierts.thatskyinteractions.feature.client.control;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.quepierts.thatskyinteractions.core.transition.BooleanTransition;
import net.quepierts.thatskyinteractions.core.transition.FloatTransition;
import net.quepierts.thatskyinteractions.feature.client.input.TsiKeys;
import net.quepierts.thatskyinteractions.feature.utils.Interpolators;
import net.quepierts.thatskyinteractions.infra.animation.tween.Tween;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Eases;

public final class CameraController {

    final BooleanTransition tUnlock = new BooleanTransition(
            Eases.CUBIC_OUT,
            0.5f
    );

    final FloatTransition tDistance = new FloatTransition(
            Eases.CUBIC_OUT,
            net.quepierts.thatskyinteractions.infra.animation.tween.interpolate.Interpolators.FLOAT,
            0.5f
    );

    final FloatTransition tXRot = new FloatTransition(
            Eases.QUAD_OUT,
            Interpolators.DEGREE,
            0.5f
    );

    final FloatTransition tYRot = new FloatTransition(
            Eases.QUAD_OUT,
            Interpolators.DEGREE,
            0.5f
    );

    int dCounter;
    int rCounter;

    public CameraController() {
        tDistance.set(4.0f);
    }

    public boolean isEnabled() {
        final var minecraft = Minecraft.getInstance();
        final var unlocked = TsiKeys.KEY_INTERACT.isDown() && !minecraft.options.getCameraType().isFirstPerson();
        tUnlock.update(Tween.GLOBAL, unlocked);
        return !unlocked;
    }

    public void turn(final float xo, final float yo) {
        float xDelta = yo * 0.15F;
        float yDelta = xo * 0.15F;

        tXRot.set(Mth.clamp(tXRot.getValue() + xDelta, -90.0F, 90.0F));
        tYRot.set(Mth.wrapDegrees(tYRot.getValue() + yDelta));

        rCounter = 0;
    }

    public void updateMaxZoom(final float distance) {
        final var d = Math.max(1.0f, distance);
        if (d < tDistance.getValue()) {
            tDistance.set(d);
        }
    }

    public void onScroll(final float scrollDeltaY) {
        dCounter = 0;

        final var distance = tDistance.getValue();
        final var clamped = Mth.clamp(distance - scrollDeltaY, 1.0f, 8.0f);

        if (Math.abs(distance - clamped) < 1e-6) {
            return;
        }

        tDistance.update(Tween.GLOBAL, clamped);
    }

    public void onComputeCameraAngles(
            final float yaw,
            final float pitch,
            final boolean mirrored,
            final AngleSetter setter
    ) {
        rCounter++;
        dCounter++;

        if (rCounter == 300) {
            this.reset(yaw, pitch);
            rCounter = -300;
        }

        if (dCounter == 300) {
            tDistance.update(Tween.GLOBAL, 4.0f);
            dCounter = -300;
        }

        final var transition = tUnlock.getValue();
        if (transition == 0.0f) {
            return;
        }

        var x = tXRot.getValue();
        var y = tYRot.getValue();

        if (mirrored) {
            x = -x;
            y += 180f;
        }

        setter.set(
                Interpolators.DEGREE.interpolate(yaw, y, transition),
                Interpolators.DEGREE.interpolate(pitch, x, transition)
        );
    }

    private void reset(final float yaw, final float pitch) {
        tXRot.update(Tween.GLOBAL, Mth.wrapDegrees(pitch));
        tYRot.update(Tween.GLOBAL, Mth.wrapDegrees(yaw));
    }

    public void onCalculateCameraDistance(
            final float currentDistance,
            final DistanceSetter setter
    ) {
        final var transition = tUnlock.getValue();
        if (transition == 0.0f) {
            return;
        }

        setter.set(Mth.lerp(transition, currentDistance, tDistance.getValue()));
    }

    @FunctionalInterface
    public interface AngleSetter {
        void set(float yaw, float pitch);
    }

    @FunctionalInterface
    public interface DistanceSetter {
        void set(float distance);
    }
}