package net.quepierts.thatskyinteractions.feature.client.gui.component.visual.button;

import lombok.RequiredArgsConstructor;
import net.minecraft.util.Mth;
import net.quepierts.thatskyinteractions.feature.client.gui.ColorStack;
import net.quepierts.thatskyinteractions.feature.client.gui.ExtendedGuiGraphics;
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
            final @NonNull Control              control,
            final @NonNull ExtendedGuiGraphics  graphics,
            final @NonNull ColorStack           colors,
            final @NonNull TweenScope           tween,

            final int                           mouseX,
            final int                           mouseY,
            final float                         delta
    ) {

        final var click         = control.getAttribute(Button.ATTRIBUTE_CLICK_TRANSITION).getValue();

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
