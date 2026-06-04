package net.quepierts.thatskyinteractions.feature.friendship.behaviour;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.friendship.model.FriendshipTreeNode;
import net.quepierts.thatskyinteractions.core.friendship.model.NodeState;
import net.quepierts.thatskyinteractions.feature.friendship.PlayerFriendshipAttachment;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FriendBehaviour implements FriendshipBehaviour {

    public static final FriendBehaviour INSTANCE    = new FriendBehaviour();
    public static final String          TYPE        = "friend";

    public static final Identifier      BE_FRIEND   = ThatSkyInteractions.location("textures/gui/be_friend.png");
    public static final Identifier      NICKNAME    = ThatSkyInteractions.location("textures/gui/nickname.png");

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
        return BE_FRIEND;
    }
}
