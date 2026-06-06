package net.quepierts.thatskyinteractions.infra.animation.tween.backend.task;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public abstract class DefaultTimingTask implements Task {

    final float duration;

    float delay;
    float elapsed;

    @Setter
    Runnable onStart;
    @Setter
    Runnable onFinish;

    TaskState state = TaskState.RUNNING;

    @Override
    public boolean update(final float delta) {
        if (this.state == TaskState.PAUSED)
            return false;

        if (this.state == TaskState.CANCELED)
            return true;

        if (this.delay > 0) {
            this.delay -= delta;
            if (this.delay <= 0) {
                this.elapsed += this.delay;
                this.delay = 0;

                if (this.onStart != null)
                    this.onStart.run();
            } else {
                return false;
            }
        }

        this.elapsed += delta;

        this._update(delta);

        if (this.elapsed >= this.duration) {
            this.state = TaskState.FINISHED;
            if (this.onFinish != null) {
                this.onFinish.run();
            }
            return true;
        }

        return false;
    }

    public void delay(final float delay) {
        this.delay = delay;
    }

    protected abstract void _update(final float delta);
}
