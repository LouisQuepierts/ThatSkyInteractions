package net.quepierts.thatskyinteractions.feature.gui.handler;

import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.quepierts.thatskyinteractions.feature.utils.DistServices;
import org.jspecify.annotations.NonNull;

public interface ClientFriendshipUiHandler {

    @NonNull ClientFriendshipUiHandler INSTANCE
            = DistServices.load(Dist.CLIENT, ClientFriendshipUiHandler.class);

    @DistServices.Default
    @NonNull ClientFriendshipUiHandler DEFAULT
            = new ClientFriendshipUiHandler() {};

    static void invite(
            @NonNull final Player requester
    ) {
        INSTANCE._invite(requester);
    }

    static void cancel(
            @NonNull final Player requester
    ) {
        INSTANCE._cancel(requester);
    }

    default void _invite(
            @NonNull final Player requester
    ) { }

    default void _cancel(
            @NonNull final Player requester
    ) { }

}
