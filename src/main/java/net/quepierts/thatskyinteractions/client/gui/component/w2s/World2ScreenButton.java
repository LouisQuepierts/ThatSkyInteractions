package net.quepierts.thatskyinteractions.client.gui.component.w2s;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;

public abstract class World2ScreenButton extends World2ScreenWidget {
    protected final ResourceLocation icon;
    protected World2ScreenButton(ResourceLocation icon) {
        this.icon = icon;
        this.selectable = true;
    }

    @Override
    public void render(GuiGraphics guiGraphics, boolean highlight, float value) {
        PoseStack pose = guiGraphics.pose();

        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        pose.pushPose();
        pose.translate(xO, yO, 100.0f);
        pose.scale(fade, fade, 1.0f);
        pose.mulPose(Axis.YP.rotation(value * Mth.TWO_PI));
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, fade);
        RenderUtils.blit(guiGraphics, highlight ? TEXTURE_HIGHLIGHT : TEXTURE_NORMAL, -16, -16, 32, 32);
        RenderUtils.blit(guiGraphics, icon, -12, -12, 24, 24);

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        pose.popPose();
    }
}
