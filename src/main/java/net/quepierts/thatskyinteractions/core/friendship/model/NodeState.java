package net.quepierts.thatskyinteractions.core.friendship.model;

public enum NodeState {
    LOCKED,
    UNLOCKABLE,
    UNLOCKED;
    
    private static final NodeState[] VALUES = values();

    public static NodeState byOrdinal(
            final byte id
    ) {
        return VALUES[id];
    }

    public static NodeState byUnlocked(
            final boolean   unlocked,
            final NodeState $default
    ) {
        return unlocked ? UNLOCKED : $default;
    }

    public static NodeState byUnlocked(
            final boolean unlocked
    ) {
        return byUnlocked(unlocked, LOCKED);
    }

    public NodeState next() {
        return VALUES[(ordinal() + 1) % VALUES.length];
    }

    public NodeState pass() {
        return this == UNLOCKED ? UNLOCKABLE : LOCKED;
    }

    public boolean hasNext() {
        return this != UNLOCKED;
    }

    public byte toByte() {
        return (byte) ordinal();
    }
}
