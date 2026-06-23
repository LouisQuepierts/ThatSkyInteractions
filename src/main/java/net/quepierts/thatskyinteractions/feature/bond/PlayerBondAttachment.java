package net.quepierts.thatskyinteractions.feature.bond;

import lombok.Getter;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.feature.registry.AttachmentTypes;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public final class PlayerBondAttachment {

    public static PlayerBondAttachment getAttachment(
            final @NonNull Player player
    ) {
        return player.getData(AttachmentTypes.PLAYER_HANDHOLDING);
    }

    @Getter
    private final PlayerHandholdRelation    handhold    = new PlayerHandholdRelation();

    @Getter
    private final PlayerCarryRelation       carry       = new PlayerCarryRelation();

    @Getter
    private final FollowerPositionResolver  resolver    = new FollowerPositionResolver();

    public PlayerHoldingHand lead(
            final @NonNull Player   follower
    ) {
        var hand = this.handhold.lead(follower);
        if (hand != PlayerHoldingHand.NONE) {
            this.resolver.reset();
        }
        return hand;
    }

    public boolean follow(
            final @NonNull Player           leader,
            final @NonNull PlayerHoldingHand leaderHand
    ) {
        if (this.handhold.follow(leader, leaderHand)) {
            this.resolver.reset();
            return true;
        }
        return false;
    }

    public PlayerHoldingHand unhold(
            final @NonNull UUID             other
    ) {
        var hand = this.handhold.unhold(other);
        if (hand != PlayerHoldingHand.NONE) {
            this.resolver.reset();
        }
        return hand;
    }

    public void unhold() {
        this.handhold.unhold();
    }

}
