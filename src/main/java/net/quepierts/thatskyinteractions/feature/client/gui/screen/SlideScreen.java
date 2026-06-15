package net.quepierts.thatskyinteractions.feature.client.gui.screen;

import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.core.property.IntProperty;
import net.quepierts.thatskyinteractions.feature.client.gui.ColorStack;
import net.quepierts.thatskyinteractions.feature.client.gui.ExtendedGuiGraphics;
import net.quepierts.thatskyinteractions.feature.client.gui.controller.ScreenController;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import org.jspecify.annotations.NonNull;

public abstract class SlideScreen<Model, Controller extends ScreenController<Model>>
        extends AnimatableScreen<Model, Controller> {

    @Getter
    private final IntProperty       sliderWide      = new IntProperty(160);

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
    public void extractAnimatableRenderState(
            final @NonNull ExtendedGuiGraphics  graphics,
            final @NonNull ColorStack           colors,
            final int                           mouseX,
            final int                           mouseY,
            final float                         delta
    ) {
        final var pose  = graphics.pose();
        final var width = this.sliderWide.get();
        final var trans = this.getTransitionValue();
        final var x     = this.width - trans * width;

        colors.push();
        colors.mul(trans, 1.0f, 1.0f, 1.0f);

        pose.pushMatrix();
        pose.translate(x, 0);
        // at the right side
        graphics.original().fill(
                0,
                0,
                width,
                this.height,
                colors.argb(0xc0, 0x10, 0x10, 0x10)
        );

        super.extractAnimatableRenderState(graphics, colors, (int) (mouseX - x), mouseY, delta);

        colors.clear();
        pose.popMatrix();
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
        final var x     = this.width - this.getTransitionValue() * width;

        return new MouseButtonEvent(
                event.x() - x,
                event.y(),
                event.buttonInfo()
        );
    }
}
