package net.quepierts.thatskyinteractions.feature.friendship.behaviour;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.friendship.model.FriendshipTreeNode;
import net.quepierts.thatskyinteractions.core.friendship.model.NodeState;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FriendBehaviour implements FriendshipBehaviour {

    public static final FriendBehaviour INSTANCE    = new FriendBehaviour();
    public static final String          TYPE        = "friend";

    public static final Identifier      BE_FRIEND   = ThatSkyInteractions.location("be_friend");
    public static final Identifier      NICKNAME    = ThatSkyInteractions.location("nickname");

    @Override
    public void execute(
            final @NonNull  ServerPlayer                requester,
            final @NonNull  ServerPlayer                receiver,
            final @NonNull  FriendshipTreeNode          node
    ) {

    }

    @Override
    public @NonNull Identifier getIcon(
            final @NonNull  Player                      player,
            final @NonNull  FriendshipTreeNode          node,
            final @NonNull  NodeState                   state
    ) {
        return state == NodeState.UNLOCKABLE ? BE_FRIEND : NICKNAME;
    }

    @Override
    public @NonNull Component getUnlockMessage(
            final @NonNull FriendshipTreeNode           node
    ) {
        return Component.translatable(
                "gui.thatskyinteractions.message.unlock.friend.request",
                Component.object(FriendshipBehaviour.SPRITE_CANDLE)
                        .withStyle(Styles.SHADOWLESS)
        ).withColor(FriendshipBehaviour.NORMAL_TEXT_COLOR);
    }
}
