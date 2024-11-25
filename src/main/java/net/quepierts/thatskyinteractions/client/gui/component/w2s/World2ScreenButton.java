package net.quepierts.thatskyinteractions.client.gui.component.w2s;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;

public abstract class World2ScreenButton extends World2ScreenWidget {
    private static final int BG_COLOR = 0x80101010;
    private static final int HL_COLOR = 0xff9ae5ff;

    protected final ResourceLocation icon;
    protected World2ScreenButton(ResourceLocation icon) {
        this.icon = icon;
        this.selectable = true;
        this.limitInScreen = true;
        this.smoothPosition = true;
    }

    @Override
    public void render(GuiGraphics guiGraphics, boolean highlight, float value, float deltaTicks) {
        PoseStack pose = guiGraphics.pose();

        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        pose.pushPose();
        pose.translate(xO, yO, 100.0f);
        pose.scale(scale, scale, 1.0f);
        pose.mulPose(Axis.YP.rotation(value * Mth.TWO_PI));
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, scale);
        RenderUtils.fillCircle(guiGraphics, -16, -16, 16, BG_COLOR);

        if (highlight) {
            RenderUtils.drawRing(guiGraphics, -16, -16, 16, 1f/32f, HL_COLOR);
        }

        this.renderInner(guiGraphics, highlight, deltaTicks);

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        pose.popPose();
    }

    protected void renderInner(GuiGraphics guiGraphics, boolean highlight, float deltaTicks) {
        RenderUtils.blit(guiGraphics, this.icon, -12, -12, 24, 24);
    }
}
