package net.quepierts.thatskyinteractions.feature.client.gui.controller;

import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.feature.client.ClientPlayerFriendshipSystem;
import net.quepierts.thatskyinteractions.feature.client.gui.ScreenLoader;
import net.quepierts.thatskyinteractions.feature.client.gui.screen.ConfirmScreen;
import net.quepierts.thatskyinteractions.feature.friendship.FriendshipTreeData;
import net.quepierts.thatskyinteractions.feature.friendship.behaviour.FriendshipBehaviourFactory;
import net.quepierts.thatskyinteractions.feature.gui.ConfirmData;

public final class FriendshipScreenController extends ScreenController<FriendshipTreeData> {

    public FriendshipScreenController(final FriendshipTreeData model) {
        super(model);
    }

    private int clicked = -1;
    private int clicks  = 0;

    public void onButtonClicked(final int index) {
        final var model         = this.getModel();
        final var structure     = model.getStructure();

        final var state         = model.getState(index);
        final var node          = structure.get(index);

        switch (state) {
            case UNLOCKABLE: {

                if (this.clicked        != index) {
                    this.clicked        = index;
                    this.clicks         = 1;
                } else {
                    this.clicks         ++;

                    if (this.clicks     >= Math.min(3, node.getCost().amount())) {
                        this            .unlock(index);
                        this.clicked    = -1;
                        this.clicks     = 0;
                    }
                }

                break;
            }
            case UNLOCKED: {

                this.interact(index);

                break;
            }
        }
    }

    private void unlock(final int index) {
        final var model         = this.getModel();
        final var node          = model.getStructure().get(index);

        final var behaviour     = FriendshipBehaviourFactory.get(node);
        final var attachment    = ClientPlayerFriendshipSystem.getLocalFriendshipData();

        final var state         = model.getState(index);
        final var icon          = behaviour.getIcon(attachment, node, state);

        final var confirm = new ConfirmData(
                icon,
                new Component[] {
                        Component.literal("test message1"),
                        Component.literal("test message2 its a long message")
                },
                () -> ClientPlayerFriendshipSystem.unlockFriendshipNode(model, index),
                null
        );

        ScreenLoader.open(ConfirmScreen.class, confirm);

    }

    private void interact(final int index) {
        ClientPlayerFriendshipSystem.interactFriendshipNode(this.getModel(), index);
    }
}
