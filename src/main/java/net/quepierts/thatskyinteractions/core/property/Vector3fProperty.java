package net.quepierts.thatskyinteractions.core.property;

import org.joml.Vector4f;
import org.joml.Vector4fc;

import java.util.function.Consumer;

public class Vector3fProperty extends Vector4f implements Consumer<Vector4fc> {

    public Vector3fProperty(float x, float y, float z, float w) {
        super(x, y, z, w);
    }

    public Vector3fProperty(Vector4fc source) {
        super(source);
    }

    public Vector3fProperty() {
    }

    @Override
    public void accept(Vector4fc value) {
        this.set(value);
    }

    public static final Vector3fProperty ZERO = new Vector3fProperty(0, 0, 0, 0);
    public static final Vector3fProperty ONE = new Vector3fProperty(1, 1, 1, 1);
}
