package net.quepierts.thatskyinteractions.feature.client.gui.screen;

import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.core.property.FloatProperty;
import net.quepierts.thatskyinteractions.core.transition.BooleanTransition;
import net.quepierts.thatskyinteractions.feature.client.gui.ColorStack;
import net.quepierts.thatskyinteractions.feature.client.gui.ExtendedGuiGraphics;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import net.quepierts.thatskyinteractions.feature.client.gui.component.layout.Layout;
import net.quepierts.thatskyinteractions.feature.client.gui.controller.ScreenController;
import net.quepierts.thatskyinteractions.infra.animation.tween.Tween;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import net.quepierts.thatskyinteractions.infra.animation.tween.backend.TweenTickHandler;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Eases;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public abstract class AnimatableScreen<Model, Controller extends ScreenController<Model>> extends Screen {

    private final TweenScope        tween;
    private final TweenTickHandler  handler;

    @Getter
    private final Controller        controller;
    @Getter(AccessLevel.PROTECTED)
    private Control                 root;
    private @Nullable Layout        layout;

    @Getter
    private final FloatProperty     transitionDuration  = new FloatProperty(0.5f);

    private final BooleanTransition transition          = new BooleanTransition(
            Eases.CUBIC_OUT,
            this.transitionDuration
    );

    @Getter
    private boolean closed;

    @Getter
    private boolean hided;

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
        this.tween      = tween;
        this.handler    = (tween != Tween.GLOBAL && tween instanceof TweenTickHandler h) ? h : null;
        this.controller = this.createController(model);
    }

    @Override
    protected void init() {
        if (this.root == null) {
            this.rebuild();
        } else if (this.layout != null) {
            this.layout.layout();
        }

        this.show();
    }

    @Override
    public void tick() {

        final var delta = Minecraft.getInstance().getDeltaTracker().getRealtimeDeltaTicks() * 0.05f;
        if (this.handler != null) {
            this.handler.tick(delta * 0.05f);
        }

        this.controller.onTick(delta);

        if (this.root != null) {
            this.root.onTick(delta);
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
            final @NonNull ExtendedGuiGraphics  graphics,
            final @NonNull ColorStack           colors,
            final int                           mouseX,
            final int                           mouseY,
            final float                         delta
    ) {
        this.root.extractRenderState(graphics, colors, mouseX, mouseY, delta);
    }

    public void hide() {
        this.hided = true;
        this.transition.update(this.tween(), false);
    }

    public void show() {
        this.hided = false;
        this.transition.update(this.tween(), true);
    }

    public TweenScope tween() {
        return this.tween;
    }

    public boolean isDiscard() {
        return this.closed && !this.isAnimating();
    }

    public boolean isAnimating() {
        return this.transition.isAnimating() || this.handler != null && this.tween.isRunning();
    }

    @Override
    public void onClose() {
        if (this.closed) {
            return;
        }
        this.hide();

        super.onClose();
        this.closed = true;
    }

    @Override
    public void removed() {
        if (this.closed) {
            return;
        }

        this.hide();
        this.closed = true;
    }

    @Override
    protected void repositionElements() {
        if (this.layout != null) {
            this.layout.layout();
        }
    }

    protected void rebuild() {
        this.root       = this.createView();
        this.layout     = (this.root instanceof Layout l) ? l : null;
    }

    protected float getTransitionValue() {
        return this.transition.getValue();
    }

    protected MouseButtonEvent remapButtonEvent(final MouseButtonEvent event) {
        return event;
    }

    protected abstract Controller createController(final Model model);

    protected abstract Control createView();
}
