package net.quepierts.thatskyinteractions.infra.animation.tween;

public interface TweenHandle {
    void cancel();

    void pause();

    void resume();

    boolean isFinished();

    boolean isPaused();

    boolean isCancelled();

    float getProgress();

    TweenHandle delay(final float duration);

    TweenHandle onStart(Runnable runtime);

    TweenHandle onFinish(Runnable runtime);
}
