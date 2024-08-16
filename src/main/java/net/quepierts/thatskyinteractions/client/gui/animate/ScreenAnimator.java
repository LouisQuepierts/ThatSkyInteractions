package net.quepierts.thatskyinteractions.client.gui.animate;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class ScreenAnimator {
    private final Set<AbstractScreenAnimation> animations;
    private float timer;

    private Runnable finish;

    public ScreenAnimator() {
        this.animations = new HashSet<>();
    }

    public void play(@NotNull AbstractScreenAnimation animation) {
        animation.play(this.timer);
        this.animations.add(animation);
    }

    public void play(@NotNull AbstractScreenAnimation animation, float delay) {
        animation.play(this.timer + delay);
        this.animations.add(animation);
    }

    public void tick() {
        this.timer += Minecraft.getInstance().getTimer().getRealtimeDeltaTicks() / 20.0f;

        if (!this.animations.isEmpty())
            this.animations.removeIf(anim -> !anim.tick(this.timer));

        if (this.isStopping()) {
            //this.animations.removeIf(AbstractScreenAnimation::cancelable);

            if (this.animations.isEmpty()) {
                finish.run();
            }
        }
    }

    public void stop(Runnable runnable) {
        //this.animations.removeIf(AbstractScreenAnimation::cancelable);
        if (this.animations.isEmpty()) {
            runnable.run();
        } else {
            this.finish = runnable;
        }
    }

    public void stop() {
        this.stop(ScreenAnimator::placeholder);
    }

    public boolean isStopping() {
        return this.finish != null;
    }

    public boolean isRunning() {
        return !isStopping() || !this.animations.isEmpty();
    }

    public float time() {
        return this.timer;
    }

    private static void placeholder() {}
}
