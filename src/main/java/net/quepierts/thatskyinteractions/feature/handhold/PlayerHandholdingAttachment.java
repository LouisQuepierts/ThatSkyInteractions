package net.quepierts.thatskyinteractions.feature.handhold;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.core.transition.FloatTransition;
import net.quepierts.thatskyinteractions.core.transition.Vector3fTransition;
import net.quepierts.thatskyinteractions.feature.registry.AttachmentTypes;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Eases;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class PlayerHandholdingAttachment {

    public static PlayerHandholdingAttachment getAttachment(
            final @NonNull Player player
    ) {
        return player.getData(AttachmentTypes.PLAYER_HANDHOLDING);
    }

    public static final int MAX_HOLDING_PLAYERS = 2;

    private final Hand      left        = new Hand();
    private final Hand      right       = new Hand();

    @Getter private boolean holding;

    private int             occupied;
    private Role            role;

    public boolean lead(
            final @NonNull Player   follower
    ) {

        if (!this.canLead(follower)) {
            return false;
        }

        if (this.left.isEmpty()) {
            this.left.hold(follower);
        } else if (this.right.isEmpty()) {
            this.right.hold(follower);
        }

        this.occupied   ++;
        this.role       = Role.LEADER;
        this.holding    = true;
        return true;
    }

    public boolean follow(
            final @NonNull Player   leader
    ) {

        if (!this.canFollow(leader)) {
            return false;
        }

        if (this.left.isEmpty()) {
            this.left.hold(leader);
        } else if (this.right.isEmpty()) {
            this.right.hold(leader);
        }

        this.occupied   ++;
        this.role       = Role.FOLLOWER;
        this.holding    = true;
        return true;
    }

    public void unhold(
            final @NonNull Player   other
    ) {

        if (this.left.player == other) {
            this.left.unhold();
            this.occupied   --;
        } else if (this.right.player == other) {
            this.right.unhold();
            this.occupied   --;
        }

        if (this.occupied == 0) {
            this.holding = false;
        }

    }

    public void unhold() {
        this.holding            = false;
        this.occupied           = 0;
        this.left.player        = null;
        this.right.player       = null;

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
        return this.left.player == player || this.right.player == player;
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
        return this.left.player != null ? this.left.player : this.right.player;
    }

    public @Nullable Player getLeft() {
        return this.left.player;
    }

    public @Nullable Player getRight() {
        return this.right.player;
    }

    @Getter
    @Setter
    private static final class Hand {
        private @Nullable Player  player;

        public void hold(
                final @NonNull Player   player
        ) {
            this.player = player;
        }

        public void unhold() {
            this.player = null;
        }

        public boolean isEmpty() {
            return this.player == null;
        }
    }

    public enum Role {
        NONE,
        LEADER,
        FOLLOWER
    }

}
