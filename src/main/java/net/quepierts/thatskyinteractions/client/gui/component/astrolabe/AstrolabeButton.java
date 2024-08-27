package net.quepierts.thatskyinteractions.client.gui.component.astrolabe;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.client.gui.animate.AnimateUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.LerpNumberAnimation;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.component.CulledRenderable;
import net.quepierts.thatskyinteractions.client.gui.holder.FloatHolder;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

@OnlyIn(Dist.CLIENT)
public abstract class AstrolabeButton extends AbstractButton implements CulledRenderable {
    private final ScreenAnimator animator;
    protected final LerpNumberAnimation clickAnimation;
    protected final FloatHolder alpha;
    private final FloatHolder click = new FloatHolder(0.0f);
    protected AstrolabeButton(int x, int y, int scale, Component message, ScreenAnimator animator, FloatHolder alpha) {
        super(x, y, scale, scale, message);
        this.animator = animator;
        this.alpha = alpha;
        this.clickAnimation = new LerpNumberAnimation(this.click, AnimateUtils.Lerp::bounce, 0.0, 1.0f, 0.15f);
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        return this.active && this.visible
                && Vector2f.distanceSquared((float) mouseX, (float) mouseY, this.getX(), this.getY()) < Mth.square(this.getWidth() / 2);
    }

    @Override
    protected final void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableBlend();

        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        final int half = width / 2;
        pose.translate(this.getX(), this.getY(), 0.0f);
        float scale = 1.0f - (AnimateUtils.Time.bounce((float) click.get()) * 0.2f);
        pose.scale(scale, scale, 1.0f);
        pose.translate(-half, -half, 0.0f);
        this.render(guiGraphics);
        pose.popPose();

        RenderSystem.disableBlend();
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.animator.play(this.clickAnimation);
        super.onClick(mouseX, mouseY);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }

    protected abstract void render(GuiGraphics guiGraphics);
}
