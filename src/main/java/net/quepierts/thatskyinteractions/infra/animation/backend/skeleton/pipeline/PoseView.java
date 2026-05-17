package net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface PoseView {

    void setPosition(final float x, final float y, final float z);

    void setRotationV(final float x, final float y, final float z);

    void setRotationQ(final float x, final float y, final float z, final float w);

    void setScale(final float x, final float y, final float z);

    void setPivot(final float x, final float y, final float z);

    void getPosition(final Vector3f out);

    void getRotationV(final Vector3f out);

    void getRotationQ(final Quaternionf out);

    void getScale(final Vector3f out);

    void getPivot(final Vector3f out);

}
