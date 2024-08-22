package net.quepierts.thatskyinteractions.client.gui.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;

public class FriendAstrolabeScreen extends Screen implements AnimatableScreen {
    private final ScreenAnimator animator = new ScreenAnimator();
    protected FriendAstrolabeScreen(Component title) {
        super(title);
    }

    @Override
    public ScreenAnimator getAnimator() {
        return this.animator;
    }
}
