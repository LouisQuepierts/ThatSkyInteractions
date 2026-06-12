package net.quepierts.thatskyinteractions.feature.friendship.behaviour;

import net.minecraft.server.level.ServerPlayer;
import net.quepierts.thatskyinteractions.core.friendship.model.FriendshipTreeNode;
import org.jspecify.annotations.NonNull;

public final class HandholdBehaviour implements FriendshipBehaviour {

    public static final HandholdBehaviour   INSTANCE    = new HandholdBehaviour();
    public static final String              TYPE        = "handhold";

    @Override
    public void execute(
            final @NonNull ServerPlayer         requester,
            final @NonNull ServerPlayer         receiver,
            final @NonNull FriendshipTreeNode   node
    ) {

    }
}
