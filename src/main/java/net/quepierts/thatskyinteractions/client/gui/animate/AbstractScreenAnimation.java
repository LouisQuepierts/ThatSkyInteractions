package net.quepierts.thatskyinteractions.client.gui.animate;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractScreenAnimation {
    private float length;
    private float begin;
    private boolean running = false;

    protected AbstractScreenAnimation(float length) {
        this.length = length;
    }

    public void play(float begin) {
        this.begin = begin;
    }

    public boolean tick(float time) {
        if (time < this.begin)
            return true;

        float delta = time - begin;
        this.run(delta);
        return (this.running = this.length == Float.POSITIVE_INFINITY || !(delta > this.length));
    }

    public boolean cancelable() {
        return true;
    }

    public boolean isRunning() {
        return running;
    }

    public float getLength() {
        return this.length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    protected abstract void run(float time);
}
