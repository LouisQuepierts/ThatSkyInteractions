package net.quepierts.thatskyinteractions.feature.client.gui.screen;

import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import net.quepierts.thatskyinteractions.feature.client.gui.component.friendship.FriendshipTreeLayout;
import net.quepierts.thatskyinteractions.feature.client.gui.component.layout.ScrollDirection;
import net.quepierts.thatskyinteractions.feature.client.gui.component.layout.VScrollPane;
import net.quepierts.thatskyinteractions.feature.client.gui.controller.FriendshipScreenController;
import net.quepierts.thatskyinteractions.feature.data.DataSyncSystem;
import net.quepierts.thatskyinteractions.feature.data.friendship.FriendshipTreeData;
import org.jspecify.annotations.NonNull;

public final class FriendshipScreen extends SlideScreen<FriendshipTreeData, FriendshipScreenController> {
    public FriendshipScreen(@NonNull final FriendshipTreeData data) {
        super(Component.translatable("gui.thatskyinteractions.friendship"), data);
    }

    @Override
    protected FriendshipScreenController createController(final FriendshipTreeData model) {
        return new FriendshipScreenController(model);
    }

    @Override
    protected Control createView() {

        final var tree      = DataSyncSystem.FRIENDSHIP_TREE.get(ThatSkyInteractions.location("friend"));
        final var layout    = new FriendshipTreeLayout(tree, 0, 0, this.getSliderWide().get(), this.height);

        final var scroll    = new VScrollPane(0, 0, this.width, this.height, Component.empty());
        scroll.addChild(layout);
        scroll.getDirection().set(ScrollDirection.BACKWARD);
        scroll.getScrollSpeed().set(16.0f);
        scroll.layout();

        return scroll;
    }
}
