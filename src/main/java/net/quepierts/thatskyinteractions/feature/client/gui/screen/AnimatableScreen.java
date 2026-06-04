package net.quepierts.thatskyinteractions.feature.client.gui.screen;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import net.quepierts.thatskyinteractions.feature.client.gui.component.layout.Layout;
import net.quepierts.thatskyinteractions.feature.client.gui.controller.ScreenController;
import net.quepierts.thatskyinteractions.infra.animation.tween.Tween;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import net.quepierts.thatskyinteractions.infra.animation.tween.backend.TweenTickHandler;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public abstract class AnimatableScreen<Model, Controller extends ScreenController<Model>> extends Screen {

    private final TweenScope    tween;

    @Getter
    private final Controller    controller;
    private Control             root;
    private @Nullable Layout    layout;

    @Getter
    private boolean closed;

    protected AnimatableScreen(
            final Component     title,
            final Model         model
    ) {
        this(title, Tween.GLOBAL, model);
    }

    protected AnimatableScreen(
            final Component     title,
            final TweenScope    tween,
            final Model         model
    ) {
        super(title);
        this.tween  = tween;
        this.controller = this.createController(model);
    }

    @Override
    protected void init() {
        this.root   = this.createView();
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
        final var remapped = this.remapButtonEvent(event);
        return this.root.mouseClicked(remapped, doubleClick);
    }

    @Override
    public boolean mouseReleased(
            final @NonNull MouseButtonEvent event
    ) {
        final var remapped = this.remapButtonEvent(event);
        return this.root.mouseReleased(remapped);
    }

    @Override
    public boolean mouseDragged(
            final @NonNull MouseButtonEvent event,
            final double dx,
            final double dy
    ) {
        if (!this.isDragging() || event.button() != 0) {
            return false;
        }

        final var remapped = this.remapButtonEvent(event);
        return this.root.mouseDragged(remapped, dx, dy);
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

    protected MouseButtonEvent remapButtonEvent(final MouseButtonEvent event) {
        return event;
    }

    protected abstract Controller createController(final Model model);

    protected abstract Control createView();
}
