package net.quepierts.thatskyinteractions.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.quepierts.thatskyinteractions.client.gui.animate.AbstractScreenAnimation;
import net.quepierts.thatskyinteractions.client.gui.animate.AnimateUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class CameraHandler {
    private final Entry rotation = Entry.create();
    private final Entry dayTime = Entry.create();

    public void rotateTo(Vector3f rotation) {
        this.rotation.src.set(this.rotation.modified);
        this.rotation.dest.set(rotation);

        ScreenAnimator.GLOBAL.play(this.rotation);
    }

    public void dayTimeTo(float time) {
        this.dayTime.src.set(this.dayTime.modified);
        this.dayTime.dest.set(time);

        ScreenAnimator.GLOBAL.play(this.dayTime);
    }

    public void resetRotation() {
        this.rotateTo(new Vector3f());
    }

    public void resetDayTime() {
        this.dayTimeTo(0);
    }

    public Vector3f getUnmodifiedRotation() {
        return new Vector3f(this.rotation.unmodified);
    }

    public Vector3f getRotation() {
        return new Vector3f(this.rotation.modified);
    }

    public Vector3f getDestRotation() {
        return new Vector3f(this.rotation.dest);
    }

    public float getUnmodifiedDayTime() {
        return this.dayTime.unmodified.x;
    }

    public void cleanup() {
        ScreenAnimator.GLOBAL.remove(this.rotation);
        ScreenAnimator.GLOBAL.remove(this.dayTime);
        this.rotation.reset();
        this.dayTime.reset();
    }

    public void onComputeCameraAngles(final ViewportEvent.ComputeCameraAngles event) {
        this.rotation.setUnmodified(event.getPitch(), event.getYaw(), event.getRoll());
        Vector3f modified = this.rotation.modified;
        event.setPitch(event.getPitch() + modified.x);
        event.setYaw(event.getYaw() + modified.y);
        event.setRoll(event.getRoll() + modified.z);
    }

    public float recomputeDayTime(float unmodified) {
        this.dayTime.unmodified.set(unmodified);
        return (unmodified + this.dayTime.modified.x) % 1.0f;
    }

    private static class Entry extends AbstractScreenAnimation {
        private final Vector3f unmodified;
        private final Vector3f src;
        private final Vector3f dest;
        private final Vector3f modified;

        private Entry(Vector3f vector3f, Vector3f src, Vector3f dest, Vector3f modified) {
            super(0.5f);
            unmodified = vector3f;
            this.src = src;
            this.dest = dest;
            this.modified = modified;
        }

        private static Entry create() {
            return new Entry(
                    new Vector3f(),
                    new Vector3f(),
                    new Vector3f(),
                    new Vector3f()
            );
        }

        public void setUnmodified(float x, float y, float z) {
            this.unmodified.set(x, y, z);
        }

        public void setUnmodified(Vector3f vector3f) {
            this.unmodified.set(vector3f);
        }

        public void reset() {
            this.src.set(0);
            this.dest.set(0);
            this.modified.set(0);
        }

        @Override
        protected void run(float time) {
            float local = time / 0.5f;
            this.modified.set(
                    AnimateUtils.Lerp.smooth(this.src.x, this.dest.x, local),
                    AnimateUtils.Lerp.smooth(this.src.y, this.dest.y, local),
                    AnimateUtils.Lerp.smooth(this.src.z, this.dest.z, local)
            );
        }
    }
}
