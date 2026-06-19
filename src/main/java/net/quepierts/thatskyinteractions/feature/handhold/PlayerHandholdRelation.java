package net.quepierts.thatskyinteractions.feature.handhold;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class PlayerHandholdRelation {

    public static final int MAX_HOLDING_PLAYERS = 2;

    private Player          left;
    private Player          right;

    @Getter
    private boolean         holding     = false;

    private int             occupied    = 0;

    @Getter
    private Role            role        = Role.NONE;

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
            final @NonNull UUID     other
    ) {

        var hand = PlayerHoldingHand.NONE;

        if (this.left != null && this.left.getUUID().equals(other)) {
            this.left       = null;
            this.occupied   --;
            hand = PlayerHoldingHand.LEFT;
        } else if (this.right != null && this.right.getUUID().equals(other)) {
            this.right      = null;
            this.occupied   --;
            hand = PlayerHoldingHand.RIGHT;
        }

        if (this.occupied == 0) {
            this.holding    = false;
            this.role       = Role.NONE;
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
                || this.isLeading()
                && !this.isHolding(follower)
                && !this.isFullyHolding();
    }

    public boolean canFollow(
            final @NonNull Player   leader
    ) {
        return this.role == Role.NONE;
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

    public @NonNull Serialized serialize() {
        if (this.role == Role.NONE) {
            return new Serialized(Role.NONE, Optional.empty(), Optional.empty());
        }

        return new Serialized(
                this.role,
                Optional.ofNullable(this.left != null ? this.left.getUUID() : null),
                Optional.ofNullable(this.right != null ? this.right.getUUID() : null)
        );
    }

    public void deserialize(
            final @NonNull Serialized   serialized,
            final @NonNull Level        level
    ) {

        this.unhold();

        switch (serialized.role) {
            case LEADER: {

                serialized.left.ifPresent(uuid -> {
                    var player = level.getPlayerByUUID(uuid);
                    if (player != null) {
                        this.lead(player);
                    }
                });

                serialized.right.ifPresent(uuid -> {
                    var player = level.getPlayerByUUID(uuid);
                    if (player != null) {
                        this.follow(player, PlayerHoldingHand.LEFT);
                    }
                });

                break;
            }
            case FOLLOWER: {

                serialized.left.ifPresentOrElse(
                        uuid -> {
                            var player = level.getPlayerByUUID(uuid);
                            if (player != null) {
                                this.follow(player, PlayerHoldingHand.LEFT);
                            }
                        },
                        () -> serialized.right.ifPresent(uuid -> {
                            var player = level.getPlayerByUUID(uuid);
                            if (player != null) {
                                this.follow(player, PlayerHoldingHand.RIGHT);
                            }
                        })
                );

                break;
            }
        }

    }

    public enum Role {
        NONE,
        LEADER,
        FOLLOWER;

        public static Role from(byte b) {
            return switch (b) {
                case 1 -> LEADER;
                case 2 -> FOLLOWER;
                default -> NONE;
            };
        }

        public byte toByte() {
            return switch (this) {
                case LEADER -> 1;
                case FOLLOWER -> 2;
                default -> 0;
            };
        }

    }

    public record Serialized(
            @NonNull Role           role,
            @NonNull Optional<UUID> left,
            @NonNull Optional<UUID> right
    ) {

        public static final StreamCodec<ByteBuf, Serialized> STREAM_CODEC
                = StreamCodec.composite(
                        ByteBufCodecs.BYTE.map(Role::from, Role::toByte),
                        Serialized::role,
                        ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC),
                        Serialized::left,
                        ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC),
                        Serialized::right,
                        Serialized::new
                );

    }

}
