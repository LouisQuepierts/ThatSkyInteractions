package net.quepierts.thatskyinteractions.infra.animation.tween.interpolate;

public interface Interpolator1f extends Interpolator<Float> {
    float interpolate(float from, float to, float progress);

    @Override
    default Float interpolate(Float from, Float to, float progress) {
        return this.interpolate(from.floatValue(), to.floatValue(), progress);
    }
}
