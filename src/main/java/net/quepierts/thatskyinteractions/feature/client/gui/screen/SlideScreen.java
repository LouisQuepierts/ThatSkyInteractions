package net.quepierts.thatskyinteractions.feature.client.gui.screen;

import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.core.property.FloatProperty;
import net.quepierts.thatskyinteractions.core.property.IntProperty;
import net.quepierts.thatskyinteractions.feature.client.gui.controller.ScreenController;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenHandle;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Eases;
import net.quepierts.thatskyinteractions.infra.animation.tween.interpolate.Interpolators;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public abstract class SlideScreen<Model, Controller extends ScreenController<Model>>
        extends AnimatableScreen<Model, Controller> {

    @Getter
    private final IntProperty       sliderWide      = new IntProperty(160);

    private final FloatProperty     transition      = new FloatProperty(0.0f);

    private @Nullable TweenHandle   slide;

    protected SlideScreen(
            final Component     title,
            final Model         model
    ) {
        super(title, model);
    }

    protected SlideScreen(
            final Component     title,
            final TweenScope    tween,
            final Model         model
    ) {
        super(title, tween, model);
    }

    @Override
    protected void init() {
        super.init();

        this.transit(1.0f);
    }

    @Override
    public void onClose() {
        super.onClose();

        this.transit(0.0f);
    }

    @Override
    public void extractAnimatableRenderState(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float delta) {
        final var pose  = graphics.pose();
        final var width = this.sliderWide.get();
        final var x     = this.width - this.transition.get() * width;

        pose.pushMatrix();
        pose.translate(x, 0);
        // at the right side
        graphics.fill(
                0,
                0,
                width,
                this.height,
                0xc0101010
        );

        super.extractAnimatableRenderState(graphics, (int) (mouseX - x), mouseY, delta);

        pose.popMatrix();
    }

    @Override
    public boolean isAnimating() {
        return this.slide != null && !this.slide.isFinished();
    }

    @Override
    public void extractBackground(
            final @NonNull GuiGraphicsExtractor graphics,
            final int mouseX,
            final int mouseY,
            final float delta
    ) {

    }

    @Override
    protected MouseButtonEvent remapButtonEvent(final MouseButtonEvent event) {
        final var width = this.sliderWide.get();
        final var x     = this.width - this.transition.get() * width;

        return new MouseButtonEvent(
                event.x() - x,
                event.y(),
                event.buttonInfo()
        );
    }

    private void transit(final float to) {

        if (this.slide != null) {
            this.slide.cancel();
            this.slide = null;
        }

        final var from = this.transition.get();
        final var diff = Math.abs(from - to);

        if (diff < 0.05f) {
            this.transition.set(to);
            return;
        }

        this.slide = this.tween().to(
                this.transition,
                from,
                to,
                diff * 0.5f,
                Interpolators.FLOAT,
                Eases.QUAD_OUT
        );

    }
}
