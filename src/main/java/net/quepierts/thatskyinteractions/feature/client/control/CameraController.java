package net.quepierts.thatskyinteractions.feature.client.control;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.quepierts.thatskyinteractions.core.property.FloatProperty;
import net.quepierts.thatskyinteractions.core.transition.BooleanTransition;
import net.quepierts.thatskyinteractions.core.transition.FloatTransition;
import net.quepierts.thatskyinteractions.feature.utils.TsiInterpolators;
import net.quepierts.thatskyinteractions.infra.animation.tween.Tween;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Eases;
import net.quepierts.thatskyinteractions.infra.animation.tween.interpolate.Interpolators;

public final class CameraController {

    private final FloatProperty pRotationTransition = new FloatProperty(0.1f);

    private final BooleanTransition tUnlock = new BooleanTransition(
            Eases.CUBIC_OUT,
            1.0f
    );

    private final FloatTransition tDistance = new FloatTransition(
            Eases.CUBIC_OUT,
            Interpolators.FLOAT,
            0.5f
    );

    private final FloatTransition tXRot = new FloatTransition(
            Eases.QUAD_OUT,
            TsiInterpolators.DEGREE,
            this.pRotationTransition
    );

    private final FloatTransition tYRot = new FloatTransition(
            Eases.QUAD_OUT,
            TsiInterpolators.DEGREE,
            this.pRotationTransition
    );

    private final FloatTransition tTargetXRot = new FloatTransition(
            Eases.QUAD_OUT,
            TsiInterpolators.DEGREE,
            this.pRotationTransition
    );

    private final FloatTransition tTargetYRot = new FloatTransition(
            Eases.QUAD_OUT,
            TsiInterpolators.DEGREE,
            this.pRotationTransition
    );

    private final FloatTransition tXOffset = new FloatTransition(
            Eases.QUAD_OUT,
            Interpolators.FLOAT,
            3.2f
    );

    private final FloatTransition tZOffset = new FloatTransition(
            Eases.QUAD_OUT,
            Interpolators.FLOAT,
            3.2f
    );

    private float targetDistance;

    private float maxZoom0;
    private float maxZoom = 4.0f;

    private boolean unlocked;

    private int dCounter;
    private int rCounter;

    private int inputCounter;
    private int inputResetCounter;

    private float yaw;
    private float pitch;
    private boolean mirrored;

    private boolean dirtyRotation;
    private boolean dirtyDistance;

    public CameraController() {
        tDistance.set(4.0f);
    }

    public boolean isLocked() {
        final var minecraft = Minecraft.getInstance();
        final var unlocked = this.unlocked && !minecraft.options.getCameraType().isFirstPerson();
        tUnlock.update(Tween.GLOBAL, unlocked);
        return !unlocked;
    }

    public void turn(final float xo, final float yo) {
        if (xo == 0.0f && yo == 0.0f) {
            return;
        }

        float xDelta = yo * 0.15F;
        float yDelta = xo * 0.15F;

        this.tTargetXRot.set(Mth.clamp(this.tTargetXRot.getValue() + xDelta, -90.0F, 90.0F));
        this.tTargetYRot.set(this.tTargetYRot.getValue() + yDelta);

        this.dirtyRotation = true;

        /*tXRot.set(Mth.clamp(tXRot.getValue() + xDelta, -90.0F, 90.0F));
        tYRot.set(Mth.wrapDegrees(tYRot.getValue() + yDelta));*/
    }

    public void updateMaxZoom(final float distance) {
        this.maxZoom0   = this.maxZoom;
        this.maxZoom    = distance;
    }

    public void onScroll(final float scrollDeltaY) {
        dCounter = 0;

        final var distance = this.targetDistance;
        final var clamped = Mth.clamp(distance - scrollDeltaY * 0.5f, 1.0f, 8.0f);

        if (Math.abs(distance - clamped) < 1e-6) {
            return;
        }

        this.targetDistance = clamped;
        this.dirtyDistance = true;
    }

    public void onComputeCameraAngles(
            final float yaw,
            final float pitch,
            final boolean mirrored,
            final AngleSetter setter
    ) {
        this.update(
                yaw,
                pitch,
                mirrored
        );

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
                TsiInterpolators.DEGREE.interpolate(yaw, y, transition),
                TsiInterpolators.DEGREE.interpolate(pitch, x, transition)
        );
    }

    public void onComputeCameraPosition(
            final double            x,
            final double            y,
            final double            z,
            final PositionSetter    setter
    ) {

        final var transition = tUnlock.getValue();
        if (transition == 0.0f) {
            return;
        }

        final var ox        = this.tXOffset.getValue();
        final var oz        = this.tZOffset.getValue();

        setter.set(
                x + ox * transition,
                y,
                z + oz * transition
        );

    }

    public void onPlayerInput(
            final boolean   sprint,
            final float     targetYRot
    ) {
        if (this.targetDistance < 2.0f) {
            return;
        }

        if (sprint) {
            this.inputCounter += 8;
        } else {
            this.inputCounter += 4;
        }

        if (this.inputCounter > 100) {
            final var factor = sprint ? 1f : 0.5f;
            // calculate x-z vector by yRot
            final var x = -Mth.sin(targetYRot * Mth.DEG_TO_RAD) * factor;
            final var z = Mth.cos(targetYRot * Mth.DEG_TO_RAD) * factor;
            this.tXOffset.update(Tween.GLOBAL, x);
            this.tZOffset.update(Tween.GLOBAL, z);
            this.inputCounter = 0;
        }

    }

    private void reset(final float yaw, final float pitch) {
//        tXRot.update(Tween.GLOBAL, Mth.wrapDegrees(pitch));
        final var wrapped = Mth.wrapDegrees(yaw);
        this.tYRot.update(Tween.GLOBAL, wrapped);
        this.tTargetYRot.update(Tween.GLOBAL, wrapped);
    }

    public void onCalculateCameraDistance(
            final float currentDistance,
            final DistanceSetter setter
    ) {
        final var transition = tUnlock.getValue();
        if (transition == 0.0f) {
            return;
        }

        final var distance = tDistance.getValue();
        setter.set(Mth.lerp(transition, currentDistance, distance));
    }

    public float getXRot() {
        return tXRot.getValue();
    }

    public float getYRot() {
        return tYRot.getValue();
    }

    public void toggle() {
        toggle(!this.unlocked);
    }

    public void toggle(boolean unlocked) {
        this.unlocked = unlocked;
    }

    private void update(
            final float     yaw,
            final float     pitch,
            final boolean   mirrored
    ) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.mirrored = mirrored;
    }

    public void update() {

        this.updateRotation(
                this.yaw,
                this.pitch,
                this.mirrored
        );
        this.updateDistance();

        if (this.targetDistance < 2.0f) {
            this.tXOffset.update(Tween.GLOBAL, 0.0f);
            this.tZOffset.update(Tween.GLOBAL, 0.0f);
            this.inputCounter = 0;
            return;
        }

        if (this.inputCounter > 0) {
            this.inputCounter -= 1;
        }

    }

    private void updateRotation(
            final float     yaw,
            final float     pitch,
            final boolean   mirrored
    ) {

        if (this.dirtyRotation) {
            this.pRotationTransition.set(0.01f);
            this.tXRot.update(Tween.GLOBAL, this.tTargetXRot.getValue());
            this.tYRot.update(Tween.GLOBAL, this.tTargetYRot.getValue());
            this.rCounter = 0;
            this.dirtyRotation = false;
            return;
        }

        this.rCounter += 1;
        if (this.rCounter == 600) {
            this.pRotationTransition.set(0.05f);
            if (mirrored) {
                this.reset(yaw + 180f, -pitch);
            } else {
                this.reset(yaw, pitch);
            }
            this.rCounter = 200;
            return;
        }

    }

    private void updateDistance() {

        if (this.dirtyDistance) {
            this.tDistance.update(Tween.GLOBAL, this.targetDistance);
            this.dCounter = 0;
            this.dirtyDistance = false;
            return;
        }

        this.dCounter += 1;

        if (this.dCounter > 600) {
            this.tDistance.update(Tween.GLOBAL, 4.0f);
            this.targetDistance = 4.0f;
            this.dCounter = 0;
            return;
        }

        if (this.dCounter > 100) {

            if (this.maxZoom > this.tDistance.getValue()) {
                this.tDistance.update(Tween.GLOBAL, this.maxZoom);
                this.targetDistance = this.maxZoom;
                this.dCounter = 0;
                return;
            }

        }

    }

    public void cleanup() {
        this.inputCounter = 0;
        this.tXRot.set(0.0f);
        this.tYRot.set(0.0f);
        this.tTargetYRot.set(0.0f);
        this.tTargetXRot.set(0.0f);
        this.tDistance.set(4.0f);
        this.tUnlock.set(false);
        this.maxZoom = 4.0f;
        this.unlocked = false;
        this.rCounter = 0;
        this.dCounter = 0;
        this.tXOffset.set(0.0f);
        this.tZOffset.set(0.0f);
    }

    @FunctionalInterface
    public interface AngleSetter {
        void set(float yaw, float pitch);
    }

    @FunctionalInterface
    public interface DistanceSetter {
        void set(float distance);
    }

    @FunctionalInterface
    public interface PositionSetter {
        void set(double x, double y, double z);
    }
}