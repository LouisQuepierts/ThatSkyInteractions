package net.quepierts.thatskyinteractions.infra.animation.tween.backend;

@FunctionalInterface
public interface TweenTickHandler {

    void tick(final float delta);

}
