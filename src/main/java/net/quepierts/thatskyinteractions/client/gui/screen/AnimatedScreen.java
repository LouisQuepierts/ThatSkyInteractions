package net.quepierts.thatskyinteractions.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.client.gui.animate.AnimateUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.LerpNumberAnimation;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.component.Resizable;
import net.quepierts.thatskyinteractions.client.gui.holder.FloatHolder;
import net.quepierts.thatskyinteractions.client.gui.layer.AnimateScreenHolderLayer;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public abstract class AnimatedScreen extends Screen implements AnimatableScreen {
    protected final ScreenAnimator animator;
    protected final FloatHolder enter;

    protected AnimatedScreen(Component title) {
        super(title);

        this.enter = new FloatHolder(0);
        this.animator = new ScreenAnimator();
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
                1.0, 0.01, 0.5f,
                false
        ));
    }

    @Override
    public ScreenAnimator getAnimator() {
        return this.animator;
    }

    @Override
    public void onClose() {
        AnimateScreenHolderLayer.INSTANCE.pop(this);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        this.width = width;
        this.height = height;

        for (GuiEventListener child : this.children()) {
            if (child instanceof Resizable resizable)
                resizable.resize(minecraft, width, height);
        }
    }
}
