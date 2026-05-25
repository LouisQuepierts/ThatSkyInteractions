package net.quepierts.thatskyinteractions.feature.client.gui.screen;

import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.infra.animation.tween.Tween;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import net.quepierts.thatskyinteractions.infra.animation.tween.backend.TweenTickHandler;
import org.jspecify.annotations.NonNull;

public abstract class AnimatableScreen extends Screen {

    private final TweenScope tween;
    @Getter
    private boolean closed;

    protected AnimatableScreen(
            final Component title
    ) {
        this(title, Tween.GLOBAL);
    }

    protected AnimatableScreen(
            final Component title,
            final TweenScope tween
    ) {
        super(title);
        this.tween  = tween;
    }

    @Override
    public final void extractRenderState(
            final @NonNull GuiGraphicsExtractor graphics,
            final int                           mouseX,
            final int                           mouseY,
            final float                         delta
    ) {
    }

    public void extractAnimatableRenderState(
            @NonNull final GuiGraphicsExtractor graphics,
            final int                           mouseX,
            final int                           mouseY,
            final float                         delta
    ) {
        if (this.tween != Tween.GLOBAL && this.tween instanceof TweenTickHandler handler) {
            handler.tick(delta * 0.05f);
        }

        super.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    public TweenScope tween() {
        return this.tween;
    }

    public boolean isDiscard() {
        return this.closed && !this.isAnimating();
    }

    public boolean isAnimating() {
        return this.tween != Tween.GLOBAL && this.tween.isRunning();
    }

    @Override
    public void onClose() {
        super.onClose();
        this.closed = true;
    }
}
