package net.quepierts.thatskyinteractions.infra.animation.tween.interpolate;

public interface Interpolator<T> {
    T interpolate(T from, T to, float progress);
}
