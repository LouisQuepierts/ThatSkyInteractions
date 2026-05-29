package net.quepierts.thatskyinteractions.feature.client.gui.component.layout;

import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.core.property.EnumProperty;
import net.quepierts.thatskyinteractions.core.property.FloatProperty;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import net.quepierts.thatskyinteractions.infra.animation.tween.Tween;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenHandle;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Eases;
import net.quepierts.thatskyinteractions.infra.animation.tween.interpolate.Interpolators;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

public class VScrollPane extends Pane {

    @Getter
    private final EnumProperty<ScrollDirection> direction   = new EnumProperty<>(ScrollDirection.FORWARD);

    @Getter
    private final FloatProperty                 spacing     = new FloatProperty(0.0f);

    @Getter
    private final FloatProperty                 scrollSpeed = new FloatProperty(4.0f);

    private final FloatProperty                 scroll0     = new FloatProperty(0.0f);
    private float scroll                                    = 0.0f;
    private float content                                   = 0.0f;

    private TweenHandle tween;

    public VScrollPane(final int x, final int y, final int width, final int height, final Component message) {
        super(x, y, width, height, message);
    }

    @Override
    public void fit() {

    }

    @Override
    public void layout() {
        this.updateContentHeight();

        if (this.direction.get() == ScrollDirection.FORWARD) {
            this.layoutForward();
        } else {
            this.layoutBackward();
        }

    }

    @Override
    protected void extractWidgetRenderState(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float delta) {

        final var pose      = graphics.pose();
        final var scroll    = this.scroll0.get() * this.direction.get().getDirection();

        final var transform = pose.transform(new Vector3f(this.getX(), this.getY(), 0.0f));

        final var left      = (int) transform.x();
        final var top       = (int) transform.y();

        graphics.enableScissor(
                left,
                top,
                left + this.getWidth(),
                top + this.getHeight()
        );

        pose.translate(0, -scroll);
        super.extractWidgetRenderState(graphics, mouseX, (int) (mouseY + scroll), delta);
        pose.translate(0, scroll);

        graphics.disableScissor();
    }

    @Override
    public boolean mouseScrolled(final double x, final double y, final double scrollX, final double scrollY) {
        if (!this.isMouseOver(x, y)) {
            return false;
        }

        final var current   = this.scroll;
        final var value     = current - (float) scrollY * this.direction.get().getDirection() * this.scrollSpeed.get();
        final var clamped   = Math.min(Math.max(value, 0.0f), this.content - this.height);

        if (clamped != current) {
            this.scroll = clamped;

            this.killAnimation();
            this.tween  = Tween.to(
                    this.scroll0,
                    this.scroll0.get(),
                    this.scroll,
                    0.3f,
                    Interpolators.FLOAT,
                    Eases.CUBIC_OUT
            );
        }
        return true;
    }

    private void layoutForward() {
        final var padding   = this.getPadding();
        final var left      = (int) padding.getLeft() + this.getX();

        var currentY = padding.getTop();
        for (final var child : this.getChildren()) {
            child.setPosition(left, (int) currentY);
            currentY += child.getHeight() + this.spacing.get();

            if (child instanceof Layout layout) {
                layout.layout();
            }
        }
    }

    private void layoutBackward() {
        final var padding   = this.getPadding();
        final var left      = (int) padding.getLeft() + this.getX();

        var currentY = this.getHeight() - padding.getBottom();
        for (final var child : this.getChildren()) {
            currentY -= child.getHeight();
            child.setPosition(left, (int) currentY);
            currentY -= this.spacing.get();

            if (child instanceof Layout layout) {
                layout.layout();
            }
        }
    }

    private void updateContentHeight() {
        this.content = this.getChildren().stream()
                .mapToInt(Control::getHeight)
                .sum() + this.getChildren().size() * this.spacing.get();
    }

    private void killAnimation() {
        if (this.tween != null) {
            this.tween.cancel();
            this.tween = null;
        }
    }

}
