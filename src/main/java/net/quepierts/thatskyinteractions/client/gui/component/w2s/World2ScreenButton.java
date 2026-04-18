package net.quepierts.thatskyinteractions.client.gui.component.w2s;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.gui.SdfGraphics;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;

public abstract class World2ScreenButton extends World2ScreenWidget {
    private static final ResourceLocation W2S_LOCATION = ThatSkyInteractions.getLocation("textures/gui/w2s.png");
    private static final int BG_COLOR = 0x80101010;
    private static final int HL_COLOR = 0xff9ae5ff;

    protected final ResourceLocation icon;
    private int alpha = 0;
    protected World2ScreenButton(ResourceLocation icon) {
        this.icon = icon;
        this.selectable = true;
        this.limitInScreen = true;
        this.smoothPosition = true;
    }

    @Override
    public void render(GuiGraphics guiGraphics, boolean highlight, float value, float deltaTicks) {
        var sdf     = SdfGraphics.getInstance();
        var pose    = guiGraphics.pose();

        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        pose.pushPose();
        pose.translate(xO, yO, 100.0f);
        pose.scale(scale, scale, scale);
        pose.mulPose(Axis.YP.rotation(value * Mth.TWO_PI));

        if (highlight && this.alpha < 10) {
            this.alpha ++;
        } else if (!highlight && this.alpha > 0) {
            this.alpha --;
        }

        if (this.alpha > 0) {
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f,  alpha / 10.0f);

            sdf     .color(BG_COLOR)
                    .circle(-16, -16, 16)
                    .fill(pose)

                    .color(0xffd1cec1)
                    .circle(-15, -15, 15)
                    .stroke(pose, 1.0f);
            /*RenderUtils.fillCircle(guiGraphics, -16, -16, 16, BG_COLOR);
            RenderUtils.drawRing(guiGraphics, -15, -15, 15, 1, 0xffd1cec1);*/

            this.renderInner(guiGraphics, highlight, deltaTicks);
        }

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        pose.translate(0, 8, 0);
        pose.mulPose(Axis.ZP.rotation(Mth.PI / 4));

        sdf     .color(BG_COLOR)
                .round(1)
                .rectangle(0, 0, 8, 8)
                .fill(pose);

//        RenderUtils.fillRoundRect(guiGraphics, 0, 0, 8, 8, 1, BG_COLOR);
        RenderUtils.blit(guiGraphics, W2S_LOCATION, 0, 0, 8, 8);

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        pose.popPose();
    }

    protected void renderInner(GuiGraphics guiGraphics, boolean highlight, float deltaTicks) {
        RenderUtils.blit(guiGraphics, this.icon, -12, -12, 24, 24);
    }
}
