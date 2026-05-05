package net.quepierts.thatskyinteractions.core.model.astrolabe;

public record AstrolabeNode(
        int x,
        int y,
        DescriptionPosition namePosition
) {


    public enum DescriptionPosition {
        UP,
        DOWN,
        LEFT,
        RIGHT;
    }
}
