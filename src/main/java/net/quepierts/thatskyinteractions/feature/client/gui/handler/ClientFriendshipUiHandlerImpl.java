package net.quepierts.thatskyinteractions.feature.client.gui.handler;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.client.ClientPlayerFriendshipSystem;
import net.quepierts.thatskyinteractions.feature.client.gui.component.floating.FloatingButton;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.floating.FloatingButtonNode;
import net.quepierts.thatskyinteractions.feature.client.gui.layer.FloatingControlLayer;
import net.quepierts.thatskyinteractions.feature.gui.handler.ClientFriendshipUiHandler;
import net.quepierts.thatskyinteractions.feature.gui.FloatingControlHandle;
import org.jspecify.annotations.NonNull;

public final class ClientFriendshipUiHandlerImpl implements ClientFriendshipUiHandler {

    @Override
    public void _invite(final @NonNull Player requester) {
        final var attachment    = ClientPlayerFriendshipSystem.getLocalFriendshipData();
        final var invite        = attachment.getInvite(requester);

        if (invite != -1) {
            return;
        }

        final var handle        = FloatingControlLayer.INSTANCE.add(
                                    FloatingButton.dynamic(
                                            Component.empty(),
                                            pos -> pos.set(
                                                    (float) requester.getX(),
                                                    (float) requester.getY() + 2.0f,
                                                    (float) requester.getZ()
                                            ),
                                            _ -> {
                                                // todo
                                            }
                                    ).withVisualNode(FloatingButtonNode.texture(
                                            ThatSkyInteractions.location("textures/gui/be_friend.png")
                                    ))
                                );
        attachment              .sendInvite(requester, handle.id());

    }

    @Override
    public void _cancel(final @NonNull Player requester) {
        final var attachment    = ClientPlayerFriendshipSystem.getLocalFriendshipData();
        final var handle        = attachment.getInvite(requester);

        if (handle == -1) {
            return;
        }

        FloatingControlLayer.INSTANCE.remove(new FloatingControlHandle(handle));
        attachment              .removeInvite(requester);

    }
}
