package net.quepierts.thatskyinteractions.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.gui.animate.AnimateUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.LerpNumberAnimation;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.component.Resizable;
import net.quepierts.thatskyinteractions.client.gui.holder.FloatHolder;
import net.quepierts.thatskyinteractions.client.gui.layer.AnimateScreenHolderLayer;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RightPoopScreen extends Screen implements AnimatableScreen {
    public static final int BG_COLOR = 0xc0101010;
    protected final ScreenAnimator animator;
    private final FloatHolder enter;

    protected final int size;

    protected RightPoopScreen(Component title, int size) {
        super(title);
        this.animator = new ScreenAnimator();
        this.enter = new FloatHolder(0);
        this.size = size;

        AnimateScreenHolderLayer.INSTANCE.open(this);
    }

    @Override
    public void enter() {
        this.animator.play(new LerpNumberAnimation(
                this.enter,
                AnimateUtils.Lerp::smooth,
                0.0, 1.0, 0.5f
        ));
    }

    @Override
    public void hide() {
        this.animator.play(new LerpNumberAnimation(
                this.enter,
                AnimateUtils.Lerp::smooth,
                1.0, 0.0, 0.5f,
                false
        ));
    }

    @Override
    public ScreenAnimator getAnimator() {
        return this.animator;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    public final void irender(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.enter.getValue() == 0.0f)
            return;

        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        RenderSystem.enableBlend();

        float alpha = Palette.getShaderAlpha();
        Palette.setShaderAlpha(this.enter.getValue());
        float v = this.width - this.size;

        renderOriginal(guiGraphics, mouseX, mouseY, partialTick);

        pose.translate((1.0f - this.enter.get()) * 40.0f + v, 0.0f, 0.0f);
        guiGraphics.fill(0, 0, this.width, this.height, BG_COLOR);

        renderLabel(guiGraphics, mouseX, mouseY, partialTick);

        Palette.setShaderAlpha(alpha);
        RenderSystem.disableBlend();
        pose.popPose();
    }

    protected void renderOriginal(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

    }

    protected void renderLabel(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        for (Renderable renderable : this.renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX - this.width + this.size, mouseY, button);
    }

    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        this.width = width;
        this.height = height;

        for (GuiEventListener child : this.children()) {
            if (child instanceof Resizable resizable)
                resizable.resize(minecraft, width, height);
        }
    }


    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    public void removed() {
        AnimateScreenHolderLayer.INSTANCE.close(this);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
