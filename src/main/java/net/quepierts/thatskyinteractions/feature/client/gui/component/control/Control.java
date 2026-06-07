package net.quepierts.thatskyinteractions.feature.client.gui.component.control;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.feature.client.gui.ColorStack;
import net.quepierts.thatskyinteractions.feature.client.gui.component.attribute.AttributeHolder;
import net.quepierts.thatskyinteractions.feature.client.gui.component.attribute.IAttributeHolder;
import net.quepierts.thatskyinteractions.feature.client.gui.component.attribute.AttributeKey;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.VisualNode;
import net.quepierts.thatskyinteractions.core.model.ui.Insets;
import net.quepierts.thatskyinteractions.feature.mixin.vanilla.client.accessor.AbstractWidgetAccessor;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import org.joml.Vector2fc;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class Control extends AbstractWidget implements IAttributeHolder {

    private static boolean              debug       = false;

    private final AttributeHolder       attributes  = new AttributeHolder();
    private final TweenScope            tween;

    @Getter
    private final Consumer<Vector2fc>   position;

    @Getter
    private final Consumer<Vector2fc>   size;

    @Getter
    private final Insets                padding;

    @Getter
    private final Insets                margin;

    @Setter
    @Getter(AccessLevel.PROTECTED)
    private VisualNode                  visualNode;

    public Control(
            final TweenScope    tween,
            final int           x,
            final int           y,
            final int           width,
            final int           height,
            final Component     message
    ) {
        super(x, y, width, height, message);

        this.tween      = tween;

        this.position   = vec2 -> this.setPosition((int) vec2.x(), (int) vec2.y());
        this.size       = vec2 -> this.setSize((int) vec2.x(), (int) vec2.y());

        this.padding    = new Insets(0);
        this.margin     = new Insets(0);
    }

    @Override
    protected final void extractWidgetRenderState(
            final @NonNull GuiGraphicsExtractor graphics,
            final int                           mouseX,
            final int                           mouseY,
            final float                         delta
    ) { }

    public final void extractRenderState(
            final @NonNull GuiGraphicsExtractor graphics,
            final @NonNull ColorStack           colors,
            final int                           mouseX,
            final int                           mouseY,
            final float                         delta
    ) {
        if (this.visible) {
            this.isHovered  = graphics.containsPointInScissor(mouseX, mouseY) && this.isMouseOver(mouseX, mouseY);
            this.extractControlRenderState(
                    graphics,
                    colors,
                    mouseX,
                    mouseY,
                    delta
            );
            ((AbstractWidgetAccessor) this).getTooltip().refreshTooltipForNextRenderPass(graphics, mouseX, mouseY, this.isHovered(), this.isFocused(), this.getRectangle());
        }
    }

    protected void extractControlRenderState(
            final @NonNull GuiGraphicsExtractor graphics,
            final @NonNull ColorStack           colors,
            final int                           mouseX,
            final int                           mouseY,
            final float                         delta
    ) {

        this.renderDebug(graphics);

        if (this.visualNode != null) {
            final var pose = graphics.pose();
            pose.pushMatrix();
            this.visualNode.extractRenderState(
                    this,
                    graphics,
                    colors,
                    this.tween,
                    mouseX,
                    mouseY,
                    delta
            );
            pose.popMatrix();
        }
    }

    public float x() {
        return this.getX();
    }

    public float y() {
        return this.getY();
    }

    @Override
    protected void updateWidgetNarration(
            final @NonNull NarrationElementOutput output
    ) {
        output.add(NarratedElementType.TITLE, this.getMessage());
    }

    protected TweenScope tween() {
        return this.tween;
    }

    @Override
    public <T> void setAttribute(
            final @NonNull AttributeKey<T>  key,
            @NonNull final T                value
    ) {
        this.attributes.setAttribute(key, value);
    }

    @Override
    public <T> T getAttribute(
            final @NonNull AttributeKey<T>  key
    ) {
        return this.attributes.getAttribute(key);
    }

    @Override
    public <T> boolean hasAttribute(
            final @NonNull AttributeKey<T>  key
    ) {
        return this.attributes.hasAttribute(key);
    }

    protected void renderDebug(
            final @NonNull GuiGraphicsExtractor graphics
    ) {
        if (Control.debug) {
            // if hover, render margin and padding
            if (true) {

                final var margin = this.getMargin();
                if (!margin.equals(Insets.NONE)) {
                    graphics.fill(
                            (int) margin.left,
                            (int) margin.top,
                            (int) (this.getRight() - margin.right),
                            (int) (this.getBottom() - margin.bottom),
                            0xFFFF0000
                    );
                }

                final var padding = this.getPadding();
                if (!padding.equals(Insets.NONE)) {
                    graphics.fill(
                            (int) (this.getX() + padding.left),
                            (int) (this.getY() + padding.top),
                            (int) (this.getRight() - padding.right),
                            (int) (this.getBottom() - padding.bottom),
                            0xFF00FF00
                    );
                }

            }
        }
    }

    public static void debug(
            final boolean debug
    ) {
        Control.debug = debug;
    }
}
