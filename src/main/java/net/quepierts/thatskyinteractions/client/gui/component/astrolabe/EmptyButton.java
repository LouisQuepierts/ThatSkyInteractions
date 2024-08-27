package net.quepierts.thatskyinteractions.client.gui.component.astrolabe;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.holder.FloatHolder;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;

public class EmptyButton extends AstrolabeButton {
    protected EmptyButton(int x, int y, ScreenAnimator animator, FloatHolder alpha) {
        super(x, y, 8, Component.empty(), animator, alpha);
    }

    @Override
    protected void render(GuiGraphics guiGraphics) {
        float alpha = Palette.getShaderAlpha();
        Palette.mulShaderAlpha(this.alpha.getValue());
        RenderUtils.drawGlowingRing(guiGraphics, 0, 0, 4, 0.12f, 0xff25223d);
        Palette.setShaderAlpha(alpha);
    }

    @Override
    public void onPress() {

    }
}
