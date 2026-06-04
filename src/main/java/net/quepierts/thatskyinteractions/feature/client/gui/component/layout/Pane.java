package net.quepierts.thatskyinteractions.feature.client.gui.component.layout;

import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.core.property.BooleanProperty;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class Pane
        extends Control
        implements Layout {

    @Getter(AccessLevel.PROTECTED)
    protected @Nullable Control   clicked;

    @Getter(AccessLevel.PROTECTED)
    private final List<Control> children;

    @Getter
    private final BooleanProperty   clip    = new BooleanProperty(false);

    public Pane(
            final @NonNull TweenScope   tween,
            final int                   x,
            final int                   y,
            final int                   width,
            final int                   height,
            final Component             message
    ) {
        super(tween, x, y, width, height, message);
        this.children = new ArrayList<>();
    }

    public void addChild(Control child) {
        this.children.add(child);
    }

    public void removeChild(Control child) {
        this.children.remove(child);
    }

    @Override
    protected void extractWidgetRenderState(
            final @NonNull GuiGraphicsExtractor graphics,

            final int                           mouseX,
            final int                           mouseY,
            final float                         delta
    ) {

        super.extractWidgetRenderState(
                graphics,
                mouseX,
                mouseY,
                delta
        );

        final var children  = this.getChildren();

        if (children.isEmpty()) {
            return;
        }

        final var clip = this.clip.get();
        if (clip) {
            final var left      = this.getY();
            final var top       = this.getX();
            graphics.enableScissor(
                    left,
                    top,
                    left + this.getWidth(),
                    top + this.getHeight()
            );
        }

        for (final var child : children) {
            child.extractRenderState(graphics, mouseX, mouseY, delta);
        }

        if (clip) {
            graphics.disableScissor();
        }
    }

    @Override
    public boolean mouseClicked(
            final @NonNull MouseButtonEvent event,
            final boolean doubleClick
    ) {
        if (!this.isActive()) {
            return false;
        }

        boolean isMouseOver = this.isMouseOver(event.x(), event.y());

        if (isMouseOver) {
            final var children = this.children;
            final var remapped = this.remapMouseButtonEvent(event);
            for (var i = children.size() - 1; i != -1; i--) {
                final var control = children.get(i);
                if (control.mouseClicked(remapped, doubleClick)) {
                    this.clicked = control;
                    return true;
                }
            }
        }

        this.clicked = null;
        return false;
    }

    @Override
    public boolean mouseReleased(
            final @NonNull MouseButtonEvent event
    ) {
        final var control = this.clicked;
        if (control != null) {
            this.clicked = null;
            final var remapped = this.remapMouseButtonEvent(event);
            return control.mouseReleased(remapped);
        }

        return false;
    }

    @Override
    public boolean mouseDragged(
            final @NonNull MouseButtonEvent event,
            final double dx,
            final double dy
    ) {
        if (this.clicked != null) {
            final var remapped = this.remapMouseButtonEvent(event);
            return this.clicked.mouseDragged(remapped, dx, dy);
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(final double x, final double y, final double scrollX, final double scrollY) {
        if (!this.isMouseOver(x, y)) {
            return false;
        }

        for (final var child : this.children) {
            if (child.mouseScrolled(x, y, scrollX, scrollY)) {
                return true;
            }
        }
        return false;
    }

    protected MouseButtonEvent remapMouseButtonEvent(final MouseButtonEvent event) {
        return event;
    }

    public abstract void fit();
}
