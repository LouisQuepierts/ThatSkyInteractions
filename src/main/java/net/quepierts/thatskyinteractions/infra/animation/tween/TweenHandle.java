package net.quepierts.thatskyinteractions.infra.animation.tween;

public interface TweenHandle {
    void cancel();

    void pause();

    void resume();

    boolean isFinished();

    boolean isPaused();

    boolean isCancelled();

    float getProgress();
}
