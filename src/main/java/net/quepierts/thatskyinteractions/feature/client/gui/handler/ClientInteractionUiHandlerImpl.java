package net.quepierts.thatskyinteractions.feature.client.gui.handler;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.client.ClientPlayerFriendshipSystem;
import net.quepierts.thatskyinteractions.feature.client.ClientPlayerInteractionSystem;
import net.quepierts.thatskyinteractions.feature.client.gui.component.floating.FloatingButton;
import net.quepierts.thatskyinteractions.feature.client.gui.component.floating.FloatingTarget;
import net.quepierts.thatskyinteractions.feature.client.gui.component.floating.WorldPositionSupplier;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.floating.FloatingButtonNode;
import net.quepierts.thatskyinteractions.feature.client.gui.layer.FloatingControlLayer;
import net.quepierts.thatskyinteractions.feature.gui.handler.ClientInteractionUiHandler;
import org.jspecify.annotations.NonNull;

public final class ClientInteractionUiHandlerImpl extends ClientInteractionUiHandler {

    @Override
    protected void _invite(final @NonNull Player requester, final @NonNull Identifier icon) {

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
                                -> ClientPlayerInteractionSystem.accept(requester)
                ).withVisualNode(FloatingButtonNode.texture(icon))
        );

    }
}
