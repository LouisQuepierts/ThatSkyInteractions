package net.quepierts.thatskyinteractions.core.friendship.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.Identifier;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public final class FriendshipTreeNode {

    private final String                id;
    private final Identifier            type;

    private final int                   left;
    private final int                   middle;
    private final int                   right;

    private final int                   parent;
    private final int                   level;

    private final Map<String, String>    metadata;

    private final Cost                  cost;

    private final Branch                branch;

    public boolean hasLeft() {
        return this.branch == Branch.MIDDLE && left != -1;
    }

    public boolean hasMiddle() {
        return middle != -1;
    }

    public boolean hasRight() {
        return this.branch == Branch.MIDDLE && right != -1;
    }

    public Cost getUnlockCost() {
        return this.cost;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public enum Branch {
        LEFT("left"),
        MIDDLE("middle"),
        RIGHT("right");

        private static final Branch[] VALUES = values();
        @Getter private final String name;

        public static Branch fromByte(byte b) {
            return VALUES[b];
        }

        public static Branch fromName(String name) {
            for (Branch branch : VALUES) {
                if (branch.name().equals(name)) {
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

}
