package net.quepierts.thatskyinteractions.core.model.ui;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class Insets {
    public static final Insets NONE = new Insets(0);

    public float left;
    public float top;
    public float right;
    public float bottom;

    public Insets(float all) {
        this(all, all, all, all);
    }

    public void set(float left, float top, float right, float bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public void set(float all) {
        this.set(all, all, all, all);
    }

    public void set(float h, float v) {
        this.set(h, v, h, v);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof Insets other &&
                this.left == other.left &&
                this.top == other.top &&
                this.right == other.right &&
                this.bottom == other.bottom;
    }
}
