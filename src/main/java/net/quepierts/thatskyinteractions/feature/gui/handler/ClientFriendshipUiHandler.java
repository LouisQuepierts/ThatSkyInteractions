package net.quepierts.thatskyinteractions.feature.gui.handler;

import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.quepierts.thatskyinteractions.feature.utils.DistServices;
import org.jspecify.annotations.NonNull;

public abstract class ClientFriendshipUiHandler {

    private static final @NonNull ClientFriendshipUiHandler INSTANCE
            = DistServices.load(Dist.CLIENT, ClientFriendshipUiHandler.class);

    @DistServices.Default
    private static final @NonNull ClientFriendshipUiHandler DEFAULT
            = new ClientFriendshipUiHandler() {};

    public static void invite(
            @NonNull final Player requester
    ) {
        INSTANCE._invite(requester);
    }

    public static void cancel(
            @NonNull final Player requester
    ) {
        INSTANCE._cancel(requester);
    }

    protected void _invite(
            @NonNull final Player requester
    ) { }

    protected void _cancel(
            @NonNull final Player requester
    ) { }

}
