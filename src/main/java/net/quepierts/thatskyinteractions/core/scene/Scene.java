package net.quepierts.thatskyinteractions.core.scene;

import lombok.NonNull;
import org.joml.*;

public final class Scene {

    private final Vector3f  origin      = new Vector3f();
    private final Matrix4f  toGlobal    = new Matrix4f();
    private final Matrix4f  toLocal     = new Matrix4f();

    public Scene() { }

    public Scene(
            final @NonNull Vector3fc    position,
            final @NonNull Quaternionfc rotation
    ) {
        this.fromObjectTransform(position, rotation);
    }

    public void fromObjectTransform(
            final @NonNull Vector3fc    position,
            final @NonNull Quaternionfc rotation
    ) {
        this.origin     .set(position);
        this.toGlobal   .identity()
                        .translate(position)
                        .rotate(rotation);
        this.toLocal    .set(this.toGlobal)
                        .invert();
    }

    public void reset() {
        this.toGlobal   .identity();
        this.toLocal    .identity();
        this.origin     .set(0, 0, 0);
    }

    public void toLocal(final Vector3f point) {
        this.toLocal.transformPosition(point);
    }

    public void toGlobal(final Vector3f point) {
        this.toGlobal.transformPosition(point);
    }

    public @NonNull Vector3fc getOrigin() {
        return this.origin;
    }


}
