package net.quepierts.thatskyinteractions.feature.client.gui.controller;

import net.quepierts.thatskyinteractions.feature.friendship.FriendshipTreeData;

public final class FriendshipScreenController extends ScreenController<FriendshipTreeData> {

    public FriendshipScreenController(final FriendshipTreeData model) {
        super(model);
    }

    public void onButtonClicked(final int index) {
        final var model         = this.getModel();
        final var structure     = model.getStructure();

    }
}
