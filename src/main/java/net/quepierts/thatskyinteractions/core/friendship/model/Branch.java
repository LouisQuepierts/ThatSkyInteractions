package net.quepierts.thatskyinteractions.core.friendship.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Branch {
    LEFT("left"),
    MIDDLE("middle"),
    RIGHT("right");

    private static final Branch[] VALUES = values();
    @Getter
    private final String name;

    public static Branch fromByte(byte b) {
        return VALUES[b];
    }

    public static Branch fromName(String name) {
        for (Branch branch : VALUES) {
            if (branch.getName().equals(name)) {
                return branch;
            }
        }
        return null;
    }

    public byte toByte() {
        return (byte) ordinal();
    }

    public String toName() {
        return name;
    }

}
