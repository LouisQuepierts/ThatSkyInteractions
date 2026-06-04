package net.quepierts.thatskyinteractions.feature.client.gui.component.layout;

import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.core.model.ui.Alignment;
import net.quepierts.thatskyinteractions.core.model.ui.HPos;
import net.quepierts.thatskyinteractions.core.model.ui.VPos;
import net.quepierts.thatskyinteractions.core.property.EnumProperty;
import net.quepierts.thatskyinteractions.core.property.FloatProperty;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import org.jspecify.annotations.NonNull;

@Getter
public class VBox extends Pane {

    private final FloatProperty spacing             = new FloatProperty(0);
    private final EnumProperty<Alignment> alignment = new EnumProperty<>(null);

    public VBox(
            final @NonNull TweenScope tween,
            final int                   x,
            final int                   y,
            final int                   width,
            final int                   height,
            final Component             message
    ) {
        super(tween, x, y, width, height, message);
    }

    public void setSpacing(float spacing) {
        this.spacing.set(spacing);
    }

    public void setAlignment(Alignment alignment) {
        this.alignment.set(alignment);
    }

    @Override
    public void fit() {
        float maxWidth      = 0;
        float totalHeight   = 0;
        final var spacing   = this.spacing.get();

        for (final var child : this.getChildren()) {
            final var margin = child.getMargin();
            maxWidth        = Math.max(maxWidth, child.getWidth() + margin.left + margin.right);
            totalHeight     += child.getHeight() + margin.top + margin.bottom + spacing;
        }

        if (!this.getChildren().isEmpty()) {
            totalHeight     -= spacing;
        }

        final var padding   = this.getPadding();

        this.setSize(
                (int) (maxWidth + padding.left + padding.right),
                (int) (totalHeight + padding.top + padding.bottom)
        );
    }


    @Override
    public void layout() {
        final var x             = this.getX();
        final var y             = this.getY();
        final var width         = this.getWidth();
        final var height        = this.getHeight();
        final var spacing       = this.spacing.get();
        final var padding       = this.getPadding();

        final var contentLeft   = x + padding.left;
        final var contentTop    = y + padding.top;
        final var contentWidth  = width - padding.left - padding.right;
        final var contentHeight = height - padding.top - padding.bottom;

        if (this.getChildren().isEmpty()) {
            return;
        }

        var top = getTop(spacing, contentTop, contentHeight);
        for (final var child : this.getChildren()) {
            final var margin = child.getMargin();
            final var childWidth = child.getWidth();
            final var childHeight = child.getHeight();

            float left = contentLeft + margin.left;
            final var alignment = this.alignment.get();
            if (alignment != null) {
                final var hpos = alignment.getHPos();
                if (hpos == HPos.CENTER) {
                    left = contentLeft + (contentWidth - childWidth) / 2;
                } else if (hpos == HPos.RIGHT) {
                    left = contentLeft + (contentWidth - childWidth);
                }
            }
            child.setPosition(
                    (int) left,
                    (int) (top + margin.top)
            );

            if (child instanceof Layout layout) {
                layout.layout();
            }

            top += childHeight + margin.top + margin.bottom + spacing;
        }
    }

    private float getTop(
            final float spacing,
            final float contentTop,
            final float contentHeight
    ) {
        float totalChildrenHeight = 0;
        for (final var child    : this.getChildren()) {
            final var margin    = child.getMargin();
            totalChildrenHeight += child.getHeight() + margin.top + margin.bottom;
        }
        totalChildrenHeight     += (this.getChildren().size() - 1) * spacing;
        float top;
        final var alignment = this.alignment.get();
        if (alignment == null || alignment.getVPos() == VPos.TOP) {
            top = contentTop;
        } else if (alignment.getVPos() == VPos.CENTER) {
            top = contentTop + (contentHeight - totalChildrenHeight) / 2;
        } else {
            top = contentTop + (contentHeight - totalChildrenHeight);
        }

        return top;
    }
}
