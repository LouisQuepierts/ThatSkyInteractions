package net.quepierts.thatskyinteractions.feature.client.gui.component.layout;

import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class Pane
        extends Control
        implements Layout {

    @Getter(AccessLevel.PROTECTED)
    private final List<Control> children;
    private @Nullable Control   clicked;

    public Pane(
            final int x,
            final int y,
            final int width,
            final int height,
            final Component message
    ) {
        super(x, y, width, height, message);
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
            final int mouseX,
            final int mouseY,
            final float delta
    ) {

        final var children  = this.getChildren();

        if (children.isEmpty()) {
            return;
        }

        graphics.enableScissor(
                this.getX(),
                this.getY(),
                this.getRight(),
                this.getBottom()
        );

        for (final var child : children) {
            child.extractRenderState(graphics, mouseX, mouseY, delta);
        }

        graphics.disableScissor();

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
            for (var i = children.size() - 1; i != -1; i--) {
                final var control = children.get(i);
                if (control.mouseClicked(event, doubleClick)) {
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
            return control.mouseReleased(event);
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
            return this.clicked.mouseDragged(event, dx, dy);
        }
        return false;
    }

    public abstract void fit();
}
