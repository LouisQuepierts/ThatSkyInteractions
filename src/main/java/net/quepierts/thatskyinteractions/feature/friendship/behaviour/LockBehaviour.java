package net.quepierts.thatskyinteractions.feature.friendship.behaviour;

import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.friendship.model.FriendshipTreeNode;
import net.quepierts.thatskyinteractions.core.friendship.model.NodeState;
import net.quepierts.thatskyinteractions.feature.friendship.PlayerFriendshipAttachment;
import org.jspecify.annotations.NonNull;

public final class LockBehaviour implements FriendshipBehaviour {

    public static final LockBehaviour   INSTANCE    = new LockBehaviour();
    public static final String          TYPE        = "lock";
    public static final Identifier      ICON        = ThatSkyInteractions.location("textures/gui/lock.png");

    @Override
    public void execute(
            final @NonNull  PlayerFriendshipAttachment  attachment,
            final @NonNull  FriendshipTreeNode          node
    ) {

    }

    @Override
    public @NonNull Identifier getIcon(
            final @NonNull  PlayerFriendshipAttachment  attachment,
            final @NonNull  FriendshipTreeNode          node,
            final @NonNull  NodeState                   state
    ) {
        return ICON;
    }

}
