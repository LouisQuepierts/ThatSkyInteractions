package net.quepierts.thatskyinteractions.client.gui.component.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.gui.animate.AnimateUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.LerpNumberAnimation;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.holder.FloatHolder;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public abstract class SqueezeButton extends AbstractButton {
    protected final ScreenAnimator animator;
    protected final ResourceLocation icon;
    protected final LerpNumberAnimation clickAnimation;
    private final FloatHolder click = new FloatHolder(0.0f);

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
        this.renderIcon(guiGraphics, -half);
        //guiGraphics.blit(icon, -half, -half, this.getWidth(), this.getHeight(), 0, 0, size, size, size, size);
        pose.popPose();

        RenderSystem.disableBlend();
    }

    protected void renderIcon(GuiGraphics guiGraphics, int begin) {
        float alpha = Palette.getShaderAlpha();

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        RenderUtils.fillRoundRect(guiGraphics, begin, begin, width, width, 0.16f, 0x80101010);
        if (!this.active) {
            RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, alpha);
        }
        RenderUtils.blit(guiGraphics, this.getIcon(), begin, begin, width, width);
    }

    @NotNull
    public ResourceLocation getIcon() {
        return this.icon;
    }
}
