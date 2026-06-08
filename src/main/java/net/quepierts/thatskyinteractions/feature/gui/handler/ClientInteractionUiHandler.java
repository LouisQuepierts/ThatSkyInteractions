package net.quepierts.thatskyinteractions.feature.gui.handler;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.quepierts.thatskyinteractions.feature.utils.DistServices;
import org.jspecify.annotations.NonNull;

public abstract class ClientInteractionUiHandler {

    private static final @NonNull ClientInteractionUiHandler INSTANCE
            = DistServices.load(Dist.CLIENT, ClientInteractionUiHandler.class);

    @DistServices.Default
    private static final @NonNull ClientInteractionUiHandler DEFAULT
            = new ClientInteractionUiHandler() {};

    public static void invite(
            final @NonNull Player       requester,
            final @NonNull Identifier   icon
    ) {
        INSTANCE._invite(requester, icon);
    }

    protected void _invite(
            final @NonNull Player       requester,
            final @NonNull Identifier   icon
    ) { }

}
