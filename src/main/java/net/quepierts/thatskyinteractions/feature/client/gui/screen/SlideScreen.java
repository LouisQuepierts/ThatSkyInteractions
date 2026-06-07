package net.quepierts.thatskyinteractions.feature.client.gui.screen;

import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.core.property.FloatProperty;
import net.quepierts.thatskyinteractions.core.property.IntProperty;
import net.quepierts.thatskyinteractions.feature.client.gui.BooleanTransition;
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

    private final BooleanTransition transition     = new BooleanTransition(
            Eases.CUBIC_OUT,
            0.5f
    );

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
    public void show() {
        super.show();
        this.transition.update(this.tween(), true);
    }

    @Override
    public void hide() {
        super.hide();
        this.transition.update(this.tween(), false);
    }

    @Override
    public void extractAnimatableRenderState(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float delta) {
        final var pose  = graphics.pose();
        final var width = this.sliderWide.get();
        final var x     = this.width - this.transition.getValue() * width;

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
        return this.transition.isAnimating();
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
        final var x     = this.width - this.transition.getValue() * width;

        return new MouseButtonEvent(
                event.x() - x,
                event.y(),
                event.buttonInfo()
        );
    }
}
