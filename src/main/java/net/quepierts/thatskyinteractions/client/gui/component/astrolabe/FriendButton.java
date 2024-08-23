package net.quepierts.thatskyinteractions.client.gui.component.astrolabe;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.client.RenderUtils;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.holder.FloatHolder;

public class FriendButton extends AstrolabeButton {
    private final FloatHolder alpha;
    public FriendButton(int x, int y, Component message, ScreenAnimator animator, FloatHolder alpha) {
        super(x, y, 12, message, animator);
        this.alpha = alpha;
    }

    @Override
    public void onPress() {

    }

    @Override
    protected void render(GuiGraphics guiGraphics) {
        float alpha = Palette.getShaderAlpha();
        Palette.mulShaderAlpha(this.alpha.getValue());
        RenderUtils.drawGlowingRing(guiGraphics, 0, 0, 6, 0.08f, 0xff25223d);
        Palette.setShaderAlpha(alpha);
    }
}
