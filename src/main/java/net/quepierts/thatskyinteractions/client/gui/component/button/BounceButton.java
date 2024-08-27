package net.quepierts.thatskyinteractions.client.gui.component.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.AnimateUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.LerpNumberAnimation;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.holder.FloatHolder;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public abstract class BounceButton extends AbstractButton {
    protected final ScreenAnimator animator;
    protected final ResourceLocation icon;
    protected final LerpNumberAnimation clickAnimation;
    private final FloatHolder click = new FloatHolder(0.0f);

    public BounceButton(int x, int y, int scale, Component message, ScreenAnimator animator, ResourceLocation icon) {
        super(x, y, scale, scale, message);
        this.animator = animator;
        this.icon = icon;
        this.clickAnimation = new LerpNumberAnimation(this.click, AnimateUtils.Lerp::linear, 0.0, 1.0f, 0.3f);
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
        RenderSystem.disableCull();

        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        final int half = width / 2;
        pose.translate(this.getX() + half, this.getY() + half, 0.0f);
        this.renderBg(guiGraphics, -half);
        float scale = 1.0f - AnimateUtils.Time.bounce(click.getValue()) * 0.3f;
        float rot = click.getValue() * Mth.TWO_PI;
        pose.scale(scale, scale, 1.0f);
        pose.translate(0.0f, 0.0f, 100.0f);
        pose.mulPose(Axis.YP.rotation(rot));
        this.renderIcon(guiGraphics, -half);
        pose.popPose();

        RenderSystem.enableCull();
    }

    protected void renderBg(GuiGraphics guiGraphics, int begin) {

    }

    protected void renderIcon(GuiGraphics guiGraphics, int begin) {
        RenderUtils.blitIcon(guiGraphics, this.getIcon(), begin, begin, this.getWidth(), this.getHeight());
    }

    @NotNull
    public ResourceLocation getIcon() {
        return this.icon;
    }
}
