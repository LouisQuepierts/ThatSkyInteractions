package net.quepierts.thatskyinteractions.infra.animation.tween.backend.task;

public final class WaitTask extends DefaultTimingTask {

    private final Runnable runnable;

    public WaitTask(
            final float duration,
            final Runnable runnable
    ) {
        super(duration);
        this.runnable = runnable;
    }

    @Override
    protected void _update(final float delta) {
        if (this.elapsed >= this.duration) {
            this.runnable.run();
            this.state = TaskState.FINISHED;
        }
    }
}
