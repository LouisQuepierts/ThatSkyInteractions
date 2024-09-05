package net.quepierts.thatskyinteractions.client.gui.component.label;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ColorInputLabel extends Vector3InputLabel {
    public ColorInputLabel(ScreenAnimator animator, int xPos, int yPos, int width, int height, int inX, int inY) {
        super(Component.literal("Color"), animator, xPos, yPos, width, Math.max(height, inY + 60), inX, inY, 0, 255);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        int left = this.getX() + this.inX;
        int top = this.getY() + this.inY + 60;
        int width = this.getWidth() - this.inX * 2;
        int height = this.getHeight() - 60 - this.inY * 2;

        if (height < 0) {
            return;
        }

        RenderSystem.enableBlend();
        int color = FastColor.ARGB32.color(
                this.xSlider.getIntValue(),
                this.ySlider.getIntValue(),
                this.zSlider.getIntValue()
        );
        RenderUtils.fillRoundRect(guiGraphics, left, top, width, height, 0.1f * height / width, color);
        RenderSystem.disableBlend();
    }
}
