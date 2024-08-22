package net.quepierts.thatskyinteractions.client;

import net.neoforged.neoforge.client.event.ViewportEvent;
import net.quepierts.simpleanimator.api.animation.keyframe.Interpolation;
import net.quepierts.thatskyinteractions.client.gui.animate.AbstractScreenAnimation;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import org.joml.Vector3f;

public class CameraHandler extends AbstractScreenAnimation {
    private final Vector3f src = new Vector3f();
    private final Vector3f dest = new Vector3f();
    private final Vector3f rotation = new Vector3f();
    public CameraHandler() {
        super(0.5f);
    }

    public void move(Vector3f rotation) {
        this.src.set(this.rotation);
        this.dest.set(rotation);

        ScreenAnimator.GLOBAL.play(this);
    }

    public void reset() {
        this.move(new Vector3f());
    }

    public void onComputeCameraAngles(final ViewportEvent.ComputeCameraAngles event) {
        event.setPitch(event.getPitch() + rotation.x);
        event.setYaw(event.getYaw() + rotation.y);
        event.setRoll(event.getRoll() + rotation.z);
    }

    @Override
    protected void run(float time) {
        float local = time / 0.5f;
        this.rotation.set(Interpolation.linerInterpolation(this.src, this.dest, local));
    }
}
