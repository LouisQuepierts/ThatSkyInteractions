package net.quepierts.thatskyinteractions.feature.friendship.behaviour;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.friendship.model.FriendshipTreeNode;
import net.quepierts.thatskyinteractions.core.friendship.model.NodeState;
import org.jspecify.annotations.NonNull;

public interface FriendshipBehaviour {

    @NonNull Identifier DEFAULT_ICON = ThatSkyInteractions.location("textures/icons/none.png");

    void execute(
            final @NonNull  Player              player,
            final @NonNull  FriendshipTreeNode  node
    );

    default @NonNull Identifier getIcon(
            final @NonNull  Player              player,
            final @NonNull  FriendshipTreeNode  node,
            final @NonNull  NodeState           state
    ) {
        return DEFAULT_ICON;
    }

}
