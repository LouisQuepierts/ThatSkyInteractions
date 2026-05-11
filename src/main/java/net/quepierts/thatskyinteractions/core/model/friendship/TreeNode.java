package net.quepierts.thatskyinteractions.core.model.friendship;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.quepierts.thatskyinteractions.core.action.Action;
import net.quepierts.thatskyinteractions.core.model.Currency;
import org.jetbrains.annotations.Nullable;

@Getter
@AllArgsConstructor
public final class TreeNode {

    private static final Action     DEFAULT_ACTION  = () -> {};
    private static final Cost       DEFAULT_COST    = new Cost(Currency.WHITE_CANDLE, 0);

    private final String    id;
    private final String    left;
    private final String    middle;
    private final String    right;
    private final String    type;

    private final Action    action;
    private final Cost      cost;

    private String          parent;

    private Branch          branch;
    private int             y;
    private int             x;

    public boolean hasLeft() {
        return this.branch == Branch.MIDDLE && !left.isEmpty();
    }

    public boolean hasMiddle() {
        return !middle.isEmpty();
    }

    public boolean hasRight() {
        return this.branch == Branch.MIDDLE && !right.isEmpty();
    }

    public void updatePosition(TreeNode other, Branch branch) {
        switch (branch) {
            case LEFT:
                this.y = other.y + 48;
                this.x += other.x - 56;
                break;
            case RIGHT:
                this.y = other.y + 48;
                this.x += other.x + 56;
                break;
            case MIDDLE:
                this.y += other.y + 32;
                this.x += other.x;
                break;
        }
    }

    public Action getAction() {
        return DEFAULT_ACTION;
    }

    public Cost getUnlockCost() {
        return DEFAULT_COST;
    }

    public enum Branch {
        LEFT,
        RIGHT,
        MIDDLE
    }

    public record Cost(
            Currency currency,
            int price
    ) {};
}
