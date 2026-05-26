package net.quepierts.thatskyinteractions.feature.client.gui.component.layout;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.core.property.PropertyEnum;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ScrollDirection implements PropertyEnum<ScrollDirection> {
    FORWARD(1),
    BACKWARD(-1);

    private static final ScrollDirection[] VALUES = values();

    @Getter
    private final int direction;

    @Override
    public ScrollDirection value(final int ordinal) {
        return VALUES[ordinal];
    }
}
