package net.quepierts.thatskyinteractions.core;

import lombok.Getter;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

@Getter
public final class TransformF implements Transform {

    private final Vector3f position;
    private final Vector3f eulerAngle;
    private final Vector3f scale;
    private final Vector3f pivot;

    private boolean translated;
    private boolean rotated;
    private boolean scaled;
    private boolean pivoted;

    private Transform parent;

    public TransformF() {
        this.position = new Vector3f();
        this.eulerAngle = new Vector3f();
        this.scale = new Vector3f();
        this.pivot = new Vector3f();
    }


    @Override
    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
    }

    @Override
    public void setRotation(float x, float y, float z) {

    }

    @Override
    public void setScale(float x, float y, float z) {

    }

    @Override
    public void setPivot(float x, float y, float z) {

    }

    @Override
    public void setParent(@Nullable Transform parent) {

    }
}
