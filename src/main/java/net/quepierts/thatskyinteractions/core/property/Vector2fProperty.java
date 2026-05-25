package net.quepierts.thatskyinteractions.core.property;

import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.util.function.Consumer;

public class Vector2fProperty extends Vector2f implements Consumer<Vector2fc> {

    public Vector2fProperty(float x, float y) {
        super(x, y);
    }

    public Vector2fProperty(Vector2fc source) {
        super(source);
    }

    public Vector2fProperty() {
    }

    @Override
    public void accept(Vector2fc value) {
        this.set(value);
    }

    public static final Vector2fProperty ZERO = new Vector2fProperty(0, 0);
    public static final Vector2fProperty ONE = new Vector2fProperty(1, 1);
}
