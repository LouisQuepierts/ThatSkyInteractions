package net.quepierts.thatskyinteractions.client.gui.component.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;
import org.jetbrains.annotations.NotNull;

public class ToggleButton extends AbstractButton {
    private static final int BG_OFF     = 0xff525252;
    private static final int BG_ON      = 0xff55b456;
    private static final int BTN_OFF    = 0xffc5c5c5;
    private static final int BTN_ON     = 0xffc4c9c2;

    private static final int HEIGHT = 20;

    private boolean on = false;

    public ToggleButton(int x, int y, int width, Component message) {
        super(x, y, Math.max(width, 32), 20, message);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableBlend();

        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        final int xMid = width / 2;
        final int yMid = height / 2;
        pose.translate(this.getX() + xMid, this.getY() + yMid, 0.0f);

        RenderUtils.fillRoundRect(guiGraphics, 0, 0, width, height, 0.5f, BG_OFF);
        RenderUtils.fillCircle(guiGraphics, 4, 4, 8, BTN_OFF);
        pose.popPose();

        RenderSystem.disableBlend();
    }

    @Override
    public void onPress() {

    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {

    }
}
