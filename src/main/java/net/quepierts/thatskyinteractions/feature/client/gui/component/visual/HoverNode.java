package net.quepierts.thatskyinteractions.feature.client.gui.component.visual;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.quepierts.thatskyinteractions.feature.client.gui.BooleanTransition;
import net.quepierts.thatskyinteractions.feature.client.gui.ColorStack;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Eases;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor(staticName = "of")
public class HoverNode implements VisualNode {

    private final @NonNull RenderOp renderOp;

    private final BooleanTransition hoverTransition = new BooleanTransition(Eases.CUBIC_OUT, 0.25f);

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

        final var hw            = control.getWidth() / 2;
        final var hh            = control.getHeight() / 2;
        final var x             = control.getX() + hw;
        final var y             = control.getY() + hh;

        final var mouseOver     = control.isMouseOver(mouseX, mouseY);
        this.hoverTransition    .update(tween, mouseOver);

        final var t             = this.hoverTransition.getValue();
        final var alpha         = (int) (255 * t);

        if (alpha != 0) {
            colors.push();
            colors.mul(alpha, 0xff, 0xfe, 0xe0);
            this.renderOp.render(
                    graphics,
                    colors,
                    x, y,
                    control.getWidth(),
                    control.getHeight()
            );
            colors.pop();
        }

    }

}
