package net.quepierts.thatskyinteractions.feature.handhold;

import lombok.Getter;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.feature.registry.AttachmentTypes;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class PlayerHandholdingAttachment {

    public static PlayerHandholdingAttachment getAttachment(
            final @NonNull Player player
    ) {
        return player.getData(AttachmentTypes.PLAYER_HANDHOLDING);
    }

    public static final int MAX_HOLDING_PLAYERS = 2;

    private Player      left;
    private Player      right;

    @Getter private boolean holding;

    private int             occupied;
    private Role            role;

    public PlayerHoldingHand lead(
            final @NonNull Player   follower
    ) {

        if (!this.canLead(follower)) {
            return PlayerHoldingHand.NONE;
        }

        var hand = PlayerHoldingHand.NONE;
        if (this.left == null) {
            this.left = follower;
            hand = PlayerHoldingHand.LEFT;
        } else if (this.right == null) {
            this.right = follower;
            hand = PlayerHoldingHand.RIGHT;
        }

        this.occupied   ++;
        this.role       = Role.LEADER;
        this.holding    = true;
        return hand;
    }

    public boolean follow(
            final @NonNull Player   leader,
            final @NonNull PlayerHoldingHand leaderHand
    ) {

        if (!this.canFollow(leader)) {
            return false;
        }

        final var hand = leaderHand.opposite();
        switch (hand) {
            case LEFT:
                this.left = leader;
                break;
            case RIGHT:
                this.right = leader;
                break;
        }

        this.occupied   ++;
        this.role       = Role.FOLLOWER;
        this.holding    = true;
        return true;
    }

    public PlayerHoldingHand unhold(
            final @NonNull Player   other
    ) {

        var hand = PlayerHoldingHand.NONE;

        if (this.left == other) {
            this.left = null;
            this.occupied   --;
            hand = PlayerHoldingHand.LEFT;
        } else if (this.right == other) {
            this.right = null;
            this.occupied   --;
            hand = PlayerHoldingHand.RIGHT;
        }

        if (this.occupied == 0) {
            this.holding = false;
        }

        return hand;

    }

    public void unhold() {
        this.holding            = false;
        this.occupied           = 0;
        this.left               = null;
        this.right              = null;

        this.role               = Role.NONE;
    }

    public boolean canLead(
            final @NonNull Player   follower
    ) {
        return this.role == Role.NONE
                || !this.isHolding(follower)
                && !this.isFullyHolding()
                && !this.isLeading();
    }

    public boolean canFollow(
            final @NonNull Player   leader
    ) {
        return this.role == Role.NONE
                || !this.isHolding(leader)
                && !this.isFullyHolding()
                && !this.isFollowing();
    }

    public boolean isHolding(
            final @NonNull Player   player
    ) {
        return this.left == player || this.right == player;
    }

    public boolean isFullyHolding() {
        return this.occupied == MAX_HOLDING_PLAYERS;
    }

    public boolean isFollowing() {
        return this.isHolding() && this.role == Role.FOLLOWER;
    }

    public boolean isLeading() {
        return this.isHolding() && this.role == Role.LEADER;
    }

    public @Nullable Player getLeader() {
        return this.left != null ? this.left : this.right;
    }

    public @Nullable Player getLeft() {
        return this.left;
    }

    public @Nullable Player getRight() {
        return this.right;
    }

    public enum Role {
        NONE,
        LEADER,
        FOLLOWER
    }

}
