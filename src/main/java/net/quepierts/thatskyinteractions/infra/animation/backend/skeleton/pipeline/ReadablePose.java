package net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline;

import net.quepierts.thatskyinteractions.infra.animation.core.adapter.TransformAccessor;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface ReadablePose {

    void getPosition(final Vector3f out);

    void getRotation(final Quaternionf out);

    void getScale(final Vector3f out);

    void getTransform(final TransformAccessor out);

}
