package net.quepierts.thatskyinteractions.feature.bond;

public enum PlayerHoldingHand {
    LEFT,
    RIGHT,
    NONE;

    public PlayerHoldingHand opposite() {
        return switch (this) {
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
            default -> NONE;
        };
    }
}
