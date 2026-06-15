package net.quepierts.thatskyinteractions.feature.client.gui.handler;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.client.ClientPlayerFriendshipSystem;
import net.quepierts.thatskyinteractions.feature.client.gui.component.floating.FloatingButton;
import net.quepierts.thatskyinteractions.feature.client.gui.component.floating.FloatingTarget;
import net.quepierts.thatskyinteractions.feature.client.gui.component.floating.WorldPositionSupplier;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.floating.FloatingButtonNode;
import net.quepierts.thatskyinteractions.feature.client.gui.layer.FloatingControlLayer;
import net.quepierts.thatskyinteractions.feature.gui.handler.ClientFriendshipUiHandler;
import org.jspecify.annotations.NonNull;

public final class ClientFriendshipUiHandlerImpl extends ClientFriendshipUiHandler {

    @Override
    protected void _invite(final @NonNull Player requester) {

        final var uuid          = requester.getUUID();
        final var target        = new FloatingTarget.Entity(uuid);
        final var layer         = FloatingControlLayer.INSTANCE;
        layer                   .remove(uuid);

        layer.add(
                target,
                FloatingButton.dynamic(
                        Component.empty(),
                        WorldPositionSupplier.entity(requester, 2.0f),
                        (_, _)
                                -> ClientPlayerFriendshipSystem.acceptUnlock(requester)
                ).withVisualNode(FloatingButtonNode.icon(
                        ThatSkyInteractions.location("be_friend")
                ))
        );

    }

    @Override
    protected void _cancel(final @NonNull Player requester) {
        FloatingControlLayer.INSTANCE.remove(requester.getUUID());
    }
}
