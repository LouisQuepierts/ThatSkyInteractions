package net.quepierts.thatskyinteractions.infra.animation.tween.backend.task;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class DefaultTimingTask implements Task {

    final float duration;

    float elapsed;

    TaskState state = TaskState.RUNNING;

    @Override
    public boolean update(final float delta) {
        if (this.state == TaskState.PAUSED)
            return false;

        if (this.state == TaskState.CANCELED)
            return true;

        this.elapsed += delta;

        this._update(delta);

        if (this.elapsed >= this.duration) {
            this.state = TaskState.FINISHED;
            return true;
        }

        return false;
    }

    protected abstract void _update(final float delta);
}
