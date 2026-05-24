package net.quepierts.thatskyinteractions.infra.animation.tween.ease;

@FunctionalInterface
public interface Ease {
    float map(float t);
}
