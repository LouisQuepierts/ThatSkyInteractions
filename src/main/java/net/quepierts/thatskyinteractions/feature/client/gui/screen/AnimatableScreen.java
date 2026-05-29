package net.quepierts.thatskyinteractions.feature.client.gui.screen;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import net.quepierts.thatskyinteractions.feature.client.gui.component.layout.Layout;
import net.quepierts.thatskyinteractions.infra.animation.tween.Tween;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import net.quepierts.thatskyinteractions.infra.animation.tween.backend.TweenTickHandler;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public abstract class AnimatableScreen extends Screen {

    private final TweenScope    tween;
    private Control             root;
    private @Nullable Layout    layout;

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
    protected void init() {
        this.root   = this.create();
        this.layout = (this.root instanceof Layout l) ? l : null;
    }

    @Override
    public void tick() {

        final var delta = Minecraft.getInstance().getDeltaTracker().getRealtimeDeltaTicks() * 0.05f;
        if (this.tween != Tween.GLOBAL && this.tween instanceof TweenTickHandler handler) {
            handler.tick(delta * 0.05f);
        }
    }

    @Override
    public final void extractRenderState(
            final @NonNull GuiGraphicsExtractor graphics,
            final int                           mouseX,
            final int                           mouseY,
            final float                         delta
    ) {
    }

    @Override
    public boolean mouseClicked(
            final @NonNull MouseButtonEvent event,
            final boolean doubleClick
    ) {
        return this.root.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(
            final @NonNull MouseButtonEvent event
    ) {
        return this.root.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(
            final @NonNull MouseButtonEvent event,
            final double dx,
            final double dy
    ) {
        return this.isDragging() && event.button() == 0 && this.root.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseScrolled(
            final double x,
            final double y,
            final double scrollX,
            final double scrollY
    ) {
        return this.root.mouseScrolled(x, y, scrollX, scrollY);
    }

    public void extractAnimatableRenderState(
            @NonNull final GuiGraphicsExtractor graphics,
            final int                           mouseX,
            final int                           mouseY,
            final float                         delta
    ) {

        this.root.extractRenderState(graphics, mouseX, mouseY, delta);
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

    @Override
    protected void repositionElements() {
        if (this.layout != null) {
            this.layout.layout();
        }
    }

    protected abstract Control create();
}
