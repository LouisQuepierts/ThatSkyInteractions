package net.quepierts.thatskyinteractions.core.model.ui;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.core.property.PropertyEnum;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Alignment implements PropertyEnum<Alignment> {
    TOP_LEFT(VPos.TOP, HPos.LEFT),
    TOP_CENTER(VPos.TOP, HPos.CENTER),
    TOP_RIGHT(VPos.TOP, HPos.RIGHT),
    CENTER_LEFT(VPos.CENTER, HPos.LEFT),
    CENTER(VPos.CENTER, HPos.CENTER),
    CENTER_RIGHT(VPos.CENTER, HPos.RIGHT),
    BOTTOM_LEFT(VPos.BOTTOM, HPos.LEFT),
    BOTTOM_CENTER(VPos.BOTTOM, HPos.CENTER),
    BOTTOM_RIGHT(VPos.BOTTOM, HPos.RIGHT),;

    static final Alignment[] VALUES = values();

    final VPos vPos;
    final HPos hPos;

    @Override
    public Alignment value(final int ordinal) {
        return VALUES[ordinal];
    }
}
