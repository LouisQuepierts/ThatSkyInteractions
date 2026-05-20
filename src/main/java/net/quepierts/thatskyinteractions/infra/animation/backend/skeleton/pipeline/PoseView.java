package net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface PoseView extends ReadablePose {

    void setPosition(final float x, final float y, final float z);

    void setRotation(final float x, final float y, final float z, final float w);

    void setScale(final float x, final float y, final float z);

    default void setPosition(final Vector3f position) {
        this.setPosition(position.x, position.y, position.z);
    }

    default void setRotation(final Quaternionf rotation) {
        this.setRotation(rotation.x, rotation.y, rotation.z, rotation.w);
    }

    default void setScale(final Vector3f scale) {
        this.setScale(scale.x, scale.y, scale.z);
    }

}
