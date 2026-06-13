package net.quepierts.thatskyinteractions.core.friendship.model;

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

    public enum Branch {
        LEFT,
        MIDDLE,
        RIGHT
    }

}
