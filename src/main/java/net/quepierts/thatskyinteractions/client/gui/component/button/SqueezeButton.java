package net.quepierts.thatskyinteractions.client.gui.component.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.RenderUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.AnimateUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.LerpNumberAnimation;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.holder.DoubleHolder;
import org.jetbrains.annotations.NotNull;

public abstract class SqueezeButton extends AbstractButton {
    public static final ResourceLocation TEXTURE_ROUND_RECT = ThatSkyInteractions.getLocation("textures/gui/round_rect.png");
    protected final ScreenAnimator animator;
    protected final ResourceLocation icon;
    protected final LerpNumberAnimation clickAnimation;
    private final DoubleHolder click = new DoubleHolder(0.0f);

    public SqueezeButton(int x, int y, int scale, Component message, ScreenAnimator animator, ResourceLocation icon) {
        super(x, y, scale, scale, message);
        this.animator = animator;
        this.icon = icon;
        this.clickAnimation = new LerpNumberAnimation(this.click, AnimateUtils.Lerp::bounce, 0.0, 1.0f, 0.3f);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible) {
            if (button == 0) {
                boolean flag = this.clicked(mouseX, mouseY);
                if (flag) {
                    this.onClick(mouseX, mouseY, button);
                    return true;
                }
            }

        }
        return false;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.animator.play(this.clickAnimation);
        super.onClick(mouseX, mouseY);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableBlend();

        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        final int half = width / 2;
        pose.translate(this.getX() + half, this.getY() + half, 0.0f);
        float scale = 1.0f - (AnimateUtils.Time.bounce((float) click.get()) * 0.3f);
        pose.scale(scale, scale, 1.0f);
        pose.translate(0.0f, 0.0f, 100.0f);
        this.renderIcon(guiGraphics, -half);
        //guiGraphics.blit(icon, -half, -half, this.getWidth(), this.getHeight(), 0, 0, size, size, size, size);
        pose.popPose();

        RenderSystem.disableBlend();
    }

    protected void renderIcon(GuiGraphics guiGraphics, int begin) {
        float alpha = Palette.getShaderAlpha();

        RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, alpha * 0.5f);
        RenderUtils.fillRoundRect(guiGraphics, begin, begin, width, width, 0.16f, 0xc0101010);
        //RenderUtils.blitIcon(guiGraphics, TEXTURE_ROUND_RECT, begin, begin, width, width);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);

        RenderUtils.blit(guiGraphics, this.getIcon(), begin, begin, width, width);
    }

    @NotNull
    public ResourceLocation getIcon() {
        return this.icon;
    }
}
