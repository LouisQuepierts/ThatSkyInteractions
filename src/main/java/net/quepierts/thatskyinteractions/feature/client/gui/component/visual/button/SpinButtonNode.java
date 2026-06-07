package net.quepierts.thatskyinteractions.feature.client.gui.component.visual.button;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;
import net.quepierts.thatskyinteractions.feature.client.gui.ColorStack;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Button;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.RenderOp;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.VisualNode;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor(staticName = "of")
public class SpinButtonNode implements VisualNode {

    private final @NonNull RenderOp renderOp;

    @Override
    public void extractRenderState(
            @NonNull final Control              control,
            @NonNull final GuiGraphicsExtractor graphics,
            @NonNull final ColorStack           colors,
            @NonNull final TweenScope           tween,

            final int                           mouseX,
            final int                           mouseY,
            final float                         delta
    ) {

        // suppose the control is a button, unchecked
        final var button        = (Button) control;
        final var click         = button.getClickProgress().get();

        final var hw            = control.getWidth() / 2;
        final var hh            = control.getHeight() / 2;

        final var pose          = graphics.pose();
        pose                    .translate(
                                    control.getX() + hw,
                                    control.getY() + hh
                                );

        final var t             = Mth.cos(4 * click * Mth.PI);
        pose                    .scale(t, 1.0f);

        this.renderOp           .render(
                                    graphics,
                colors, 0, 0,
                                    control.getWidth(),
                                    control.getHeight()
        );

    }
}
