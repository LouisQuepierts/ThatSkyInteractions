package net.quepierts.thatskyinteractions.client.gui.animate;

import org.jetbrains.annotations.NotNull;

public class WaitAnimation extends AbstractScreenAnimation {
    @NotNull
    private final Runnable runnable;

    public WaitAnimation(float length, @NotNull Runnable runnable) {
        super(length);
        this.runnable = runnable;
    }

    @Override
    protected void run(float time) {
        if (time >= getLength()) {
            runnable.run();
        }
    }
}
