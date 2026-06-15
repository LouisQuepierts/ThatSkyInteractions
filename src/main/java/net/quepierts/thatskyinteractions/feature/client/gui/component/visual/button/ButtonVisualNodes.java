package net.quepierts.thatskyinteractions.feature.client.gui.component.visual.button;

import lombok.experimental.UtilityClass;
import net.minecraft.util.Mth;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Button;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.RenderOp;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.VisualNode;
import org.jspecify.annotations.NonNull;

@UtilityClass
public class ButtonVisualNodes {

    public static VisualNode base(
            final @NonNull  VisualNode[]    nodes,
            final           float           hoverFactor,
            final           float           pressFactor
    ) {

        final var cHoverFactor              = Math.max(hoverFactor, 0);
        final var cPressFactor              = Math.min(pressFactor, 1);

        return (control, graphics, colors, tween, mouseX, mouseY, delta) -> {

            final var hover                 = control.getAttribute(Button.ATTRIBUTE_HOVER_TRANSITION).getValue();
            final var press                 = control.getAttribute(Button.ATTRIBUTE_PRESS_TRANSITION).getValue();

            final var hovering              = hover > 0;
            final var pressing              = press > 0;

            final var translate             = hovering || pressing;

            final var pose                  = graphics.pose();
            final var hh                    = control.getHeight() / 2f;
            final var hw                    = control.getWidth() / 2f;

            pose                            .translate(control.getX() + hh, control.getY() + hw);


            if (hovering) {
                final var factor            = 1.0f + cHoverFactor * hover;
                pose                        .scale(factor, factor);
            }

            if (pressing) {
                final var factor            = 1.0f - cPressFactor * press;
                pose                        .scale(factor, factor);
            }

            VisualNode.array(
                    nodes,
                    control,
                    graphics,
                    colors,
                    tween,
                    mouseX,
                    mouseY,
                    delta
            );

        };

    }

    public static VisualNode base(
            final @NonNull  VisualNode[]    nodes
    ) {
        return ButtonVisualNodes.base(
                nodes,
                0.1f,
                0.25f
        );
    }

    public static VisualNode spin(
            final @NonNull RenderOp renderOp
    ) {
        return (control, graphics, colors, _, _, _, _) -> {
            final var click         = control.getAttribute(Button.ATTRIBUTE_CLICK_TRANSITION).getValue();

            if (click > 0) {
                final var pose = graphics.pose();
                final var t = Mth.cos(4 * click * Mth.PI);
                pose.scale(t, 1.0f);
            }
            renderOp                .render(
                    graphics,
                    colors, 0, 0,
                    control.getWidth(),
                    control.getHeight()
            );
        };
    }

    public static VisualNode hover(
            final @NonNull RenderOp renderOp
    ) {
        return (control, graphics, colors, _, _, _, _) -> {

            final var t             = control.getAttribute(Button.ATTRIBUTE_HOVER_TRANSITION).getValue();
            final var alpha         = (int) (255 * t);

            if (alpha != 0) {
                colors.push();
                colors.mul(alpha, 0xff, 0xfe, 0xe0);
                renderOp.render(
                        graphics,
                        colors,
                        0, 0,
                        control.getWidth(),
                        control.getHeight()
                );
                colors.pop();
            }
        };
    }

}
