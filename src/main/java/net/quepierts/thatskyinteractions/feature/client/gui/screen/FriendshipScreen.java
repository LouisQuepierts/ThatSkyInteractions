package net.quepierts.thatskyinteractions.feature.client.gui.screen;

import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import net.quepierts.thatskyinteractions.feature.client.gui.component.sky.friendship.FriendshipTreeComponentFactory;
import net.quepierts.thatskyinteractions.feature.client.gui.component.sky.friendship.FriendshipTreeLayout;
import net.quepierts.thatskyinteractions.feature.client.gui.component.layout.ScrollDirection;
import net.quepierts.thatskyinteractions.feature.client.gui.component.layout.VScrollPane;
import net.quepierts.thatskyinteractions.feature.client.gui.controller.FriendshipScreenController;
import net.quepierts.thatskyinteractions.feature.friendship.FriendshipTreeData;
import org.jspecify.annotations.NonNull;

public final class FriendshipScreen extends SlideScreen<FriendshipTreeData, FriendshipScreenController> {
    public FriendshipScreen(final @NonNull FriendshipTreeData data) {
        super(Component.translatable("gui.thatskyinteractions.friendship"), data);
    }

    @Override
    protected FriendshipScreenController createController(final FriendshipTreeData model) {
        return new FriendshipScreenController(model);
    }

    @Override
    protected Control createView() {
        final var tween         = this.tween();

        final var controller    = this.getController();
        final var component     = FriendshipTreeComponentFactory.create(tween, controller);
        final var layout        = new FriendshipTreeLayout(
                                    tween,
                                    component,
                                    0, 0,
                                    this.getSliderWide().get()
                                );

        final var scroll        = new VScrollPane(
                                    tween,
                                    0, 0,
                                    this.width, this.height,
                                    Component.empty()
                                );

        scroll                  .addChild(layout);
        scroll                  .getDirection().set(ScrollDirection.BACKWARD);
        scroll                  .getScrollSpeed().set(16.0f);
        scroll                  .layout();

        return scroll;
    }

    @Override
    protected void repositionElements() {
        this.getRoot().setSize(this.width, this.height);
        super.repositionElements();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
