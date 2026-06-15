package net.quepierts.thatskyinteractions.feature.friendship.behaviour;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.friendship.model.FriendshipTreeNode;
import net.quepierts.thatskyinteractions.core.friendship.model.NodeState;
import org.jspecify.annotations.NonNull;

public final class LockBehaviour implements FriendshipBehaviour {

    public static final LockBehaviour   INSTANCE    = new LockBehaviour();
    public static final String          TYPE        = "lock";
    public static final Identifier      ICON        = ThatSkyInteractions.location("lock");

    @Override
    public void execute(
            final @NonNull ServerPlayer                 requester,
            final @NonNull ServerPlayer                 receiver,
            final @NonNull FriendshipTreeNode           node
    ) {

    }

    @Override
    public @NonNull Identifier getIcon(
            final @NonNull  Player                      player,
            final @NonNull  FriendshipTreeNode          node,
            final @NonNull  NodeState                   state
    ) {
        return ICON;
    }

    @Override
    public @NonNull Component getUnlockMessage(
            final @NonNull FriendshipTreeNode           node
    ) {
        return Component.translatable(
                "gui.thatskyinteractions.message.unlock.lock.request",
                Component.object(FriendshipBehaviour.SPRITE_CANDLE)
                        .withStyle(Styles.SHADOWLESS),
                Component.translatable("gui.thatskyinteractions.message.unlock.lock.intimacy")
                        .withStyle(Styles.BOLD)
                        .withColor(FriendshipBehaviour.HIGHLIGHT_TEXT_COLOR)
        ).withColor(FriendshipBehaviour.NORMAL_TEXT_COLOR);
    }

}
