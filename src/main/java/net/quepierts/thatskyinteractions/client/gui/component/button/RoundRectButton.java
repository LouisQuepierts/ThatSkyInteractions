package net.quepierts.thatskyinteractions.client.gui.component.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
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
public abstract class RoundRectButton extends AbstractButton {
    protected final ScreenAnimator animator;
    protected final LerpNumberAnimation clickAnimation;
    private final FloatHolder click = new FloatHolder(0.0f);

    public RoundRectButton(int x, int y, int width, int height, Component message, ScreenAnimator animator) {
        super(x, y, width, height, message);
        this.animator = animator;
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
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {

    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableBlend();

        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        final int xMid = width / 2;
        final int yMid = height / 2;
        pose.translate(this.getX() + xMid, this.getY() + yMid, 0.0f);
        float scale = 1.0f - (AnimateUtils.Time.bounce((float) click.get()) * 0.3f);
        pose.scale(scale, scale, 1.0f);
        RenderUtils.fillRoundRect(guiGraphics, -xMid, -yMid, width, height, 0.25f * height / width, this.isHovered() ? 0xb0101010 : 0x80101010);
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, this.getMessage(), 0, -4, Palette.NORMAL_TEXT_COLOR);
        pose.popPose();

        RenderSystem.disableBlend();
    }
}
