package net.quepierts.thatskyinteractions.feature.client.gui.controller;

import net.quepierts.thatskyinteractions.feature.client.ClientPlayerFriendshipSystem;
import net.quepierts.thatskyinteractions.feature.friendship.FriendshipTreeData;

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
        ClientPlayerFriendshipSystem.unlockFriendshipNode(this.getModel(), index);
    }

    private void interact(final int index) {
        ClientPlayerFriendshipSystem.interactFriendshipNode(this.getModel(), index);
    }
}
