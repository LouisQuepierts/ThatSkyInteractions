package net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface PoseView {

    void setPosition(final float x, final float y, final float z);

    void setRotationV(final float x, final float y, final float z);

    void setRotationQ(final float x, final float y, final float z, final float w);

    void setScale(final float x, final float y, final float z);

    default void setPosition(final Vector3f position) {
        this.setPosition(position.x, position.y, position.z);
    }

    default void setRotationV(final Vector3f rotation) {
        this.setRotationV(rotation.x, rotation.y, rotation.z);
    }

    default void setRotationQ(final Quaternionf rotation) {
        this.setRotationQ(rotation.x, rotation.y, rotation.z, rotation.w);
    }

    default void setScale(final Vector3f scale) {
        this.setScale(scale.x, scale.y, scale.z);
    }

    void getPosition(final Vector3f out);

    void getRotationV(final Vector3f out);

    void getRotationQ(final Quaternionf out);

    void getScale(final Vector3f out);

}
