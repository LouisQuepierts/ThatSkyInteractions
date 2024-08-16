package net.quepierts.thatskyinteractions.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;

@OnlyIn(Dist.CLIENT)
public interface AnimatableScreen {
    ScreenAnimator getAnimator();

    default void enter() {}

    default void hide() {}

    default void irender(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {}
}
