package net.quepierts.thatskyinteractions.infra.animation.tween.backend.task;

import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenHandle;

@RequiredArgsConstructor(staticName = "of")
public final class DefaultTweenHandle implements TweenHandle {

    private final DefaultTimingTask task;

    @Override
    public void cancel() {
        this.task.state = TaskState.CANCELED;
    }

    @Override
    public void pause() {
        this.task.state = TaskState.PAUSED;
    }

    @Override
    public void resume() {
        if (this.task.state == TaskState.PAUSED) {
            this.task.state = TaskState.RUNNING;
        }
    }

    @Override
    public boolean isFinished() {
        return this.task.state == TaskState.FINISHED;
    }

    @Override
    public boolean isPaused() {
        return this.task.state == TaskState.PAUSED;
    }

    @Override
    public boolean isCancelled() {
        return this.task.state == TaskState.CANCELED;
    }

    @Override
    public float getProgress() {
        return this.task.elapsed / this.task.duration;
    }
}
