package net.quepierts.thatskyinteractions.feature.friendship;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.core.model.Currency;
import net.quepierts.thatskyinteractions.core.model.friendship.Cost;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public final class FriendshipTreeNode {

    private static final Cost DEFAULT_COST    = new Cost(Currency.WHITE_CANDLE, 0);

    private final String    id;
    private final String    type;

    private final int       left;
    private final int       middle;
    private final int       right;

    private final int       parent;
    private final int       level;

    private final Map<String, String> metadata;

    private final Cost      cost;

    private final Branch    branch;

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
        return DEFAULT_COST;
    }

    public enum Branch {
        LEFT,
        MIDDLE,
        RIGHT
    }

}
