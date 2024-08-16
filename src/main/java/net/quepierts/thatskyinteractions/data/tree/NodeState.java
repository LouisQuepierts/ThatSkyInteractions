package net.quepierts.thatskyinteractions.data.tree;

public enum NodeState {
    LOCKED,
    UNLOCKABLE,
    UNLOCKED;

    public static NodeState getNextState(NodeState type) {
        return type == UNLOCKED ? UNLOCKABLE : LOCKED;
    }

    public static NodeState byUnlocked(boolean unlocked) {
        return unlocked ? UNLOCKED : UNLOCKABLE;
    }

    public static NodeState byUnlocked(boolean unlocked, NodeState def) {
        return unlocked ? UNLOCKED : def;
    }

    public static NodeState byOrdinal(byte aByte) {
        return values()[aByte];
    }
}
