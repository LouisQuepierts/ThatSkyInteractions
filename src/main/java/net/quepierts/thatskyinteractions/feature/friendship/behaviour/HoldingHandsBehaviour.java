package net.quepierts.thatskyinteractions.feature.friendship.behaviour;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.friendship.model.FriendshipTreeNode;
import net.quepierts.thatskyinteractions.core.friendship.model.NodeState;
import net.quepierts.thatskyinteractions.feature.gui.packet.PlayerInteractionUiPacket;
import net.quepierts.thatskyinteractions.feature.handhold.PlayerHandholdingSystem;
import net.quepierts.thatskyinteractions.feature.interaction.PlayerInteractionSystem;
import org.jspecify.annotations.NonNull;

public final class HoldingHandsBehaviour implements FriendshipBehaviour {

    public static final HoldingHandsBehaviour   INSTANCE    = new HoldingHandsBehaviour();
    public static final Identifier              ICON_HOLD   = ThatSkyInteractions.location("holding_hands");
    public static final Identifier              ICON_UNHOLD = ThatSkyInteractions.location("unholding_hands");
    public static final Identifier              INTERACTION = ThatSkyInteractions.location("holding_hands");
    public static final String                  TYPE        = "holding_hands";

    @Override
    public void execute(
            final @NonNull ServerPlayer         requester,
            final @NonNull ServerPlayer         receiver,
            final @NonNull FriendshipTreeNode   node
    ) {

        PlayerInteractionSystem.invite(
                requester,
                receiver,
                INTERACTION
        );

        PacketDistributor.sendToPlayer(
                receiver,
                PlayerInteractionUiPacket.invite(
                        requester,
                        ICON_HOLD
                )
        );
    }

    @Override
    public @NonNull Identifier getIcon(
            final @NonNull  Player                      player,
            final @NonNull  FriendshipTreeNode          node,
            final @NonNull  NodeState                   state
    ) {
        final var handhold = PlayerHandholdingSystem.getAttachment(player);
        return handhold.getRelation().isLeading() ? ICON_UNHOLD : ICON_HOLD;
    }
}
