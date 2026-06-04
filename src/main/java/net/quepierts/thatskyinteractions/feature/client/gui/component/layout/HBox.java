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
public class HBox extends Pane {

    private final FloatProperty spacing             = new FloatProperty(0);
    private final EnumProperty<Alignment> alignment = new EnumProperty<>(null);

    public HBox(
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
        float totalWidth    = 0;
        float maxHeight     = 0;
        final var spacing   = this.spacing.get();

        for (final var child : this.getChildren()) {
            final var margin = child.getMargin();
            totalWidth      += child.getWidth() + margin.left + margin.right + spacing;
            maxHeight       = Math.max(maxHeight, child.getHeight() + margin.top + margin.bottom);
        }

        if (!this.getChildren().isEmpty()) {
            totalWidth      -= spacing;
        }

        final var padding   = this.getPadding();

        this.setSize(
                (int) (totalWidth + padding.left + padding.right),
                (int) (maxHeight + padding.top + padding.bottom)
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

        var left = getLeft(spacing, contentLeft, contentWidth);
        for (final var child : this.getChildren()) {
            final var margin = child.getMargin();
            final var childWidth = child.getWidth();
            final var childHeight = child.getHeight();

            float top = contentTop + margin.top;
            final var alignment = this.alignment.get();
            if (alignment != null) {
                final var vpos = alignment.getVPos();
                if (vpos == VPos.CENTER) {
                    top = contentTop + (contentHeight - childHeight) / 2;
                } else if (vpos == VPos.BOTTOM) {
                    top = contentTop + (contentHeight - childHeight);
                }
            }

            child.setPosition(
                    (int) (left + margin.left),
                    (int) top
            );

            if (child instanceof Layout layout) {
                layout.layout();
            }

            left += childWidth + margin.left + margin.right + spacing;
        }
    }

    private float getLeft(
            final float spacing,
            final float contentLeft,
            final float contentWidth
    ) {
        float totalChildrenWidth = 0;
        for (final var child : this.getChildren()) {
            final var margin = child.getMargin();
            totalChildrenWidth += child.getWidth() + margin.left + margin.right;
        }
        totalChildrenWidth += (this.getChildren().size() - 1) * spacing;

        float left;
        final var alignment = this.alignment.get();
        if (alignment == null || alignment.getHPos() == HPos.LEFT) {
            left = contentLeft;
        } else if (alignment.getHPos() == HPos.CENTER) {
            left = contentLeft + (contentWidth - totalChildrenWidth) / 2;
        } else {
            left = contentLeft + (contentWidth - totalChildrenWidth);
        }

        return left;
    }

}
