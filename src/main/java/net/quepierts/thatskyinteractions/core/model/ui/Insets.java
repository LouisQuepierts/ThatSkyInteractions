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
}
