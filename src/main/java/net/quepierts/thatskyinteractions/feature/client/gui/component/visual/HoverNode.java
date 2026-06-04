package net.quepierts.thatskyinteractions.feature.client.gui.component.visual;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.quepierts.thatskyinteractions.core.property.IntProperty;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenHandle;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Eases;
import net.quepierts.thatskyinteractions.infra.animation.tween.interpolate.Interpolators;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor(staticName = "of")
public class HoverNode implements VisualNode {

    private final @NonNull RenderOp renderOp;

    private final IntProperty   hoverProperty   = new IntProperty(0);
    private TweenHandle         hoverHandle;
    private boolean             hovering;

    @Override
    public void extractRenderState(
            @NonNull final Control              control,
            @NonNull final GuiGraphicsExtractor graphics,
            @NonNull final TweenScope           tween,

            final int                           mouseX,
            final int                           mouseY,
            final float                         delta
    ) {

        final var hw            = control.getWidth() / 2;
        final var hh            = control.getHeight() / 2;
        final var x             = control.getX() + hw;
        final var y             = control.getY() + hh;

        final var alpha         = this.hoverProperty.get();
        final var mouseOver     = control.isMouseOver(mouseX, mouseY);
        if (mouseOver && !this.hovering) {
            this.hovering = true;
            if (this.hoverHandle != null) {
                this.hoverHandle.cancel();
            }
            this.hoverHandle = tween.to(
                    this.hoverProperty,
                    alpha,
                    255,
                    0.25f,
                    Interpolators.INT,
                    Eases.CUBIC_OUT
            );
        } else if (!mouseOver && this.hovering) {
            this.hovering = false;
            if (this.hoverHandle != null) {
                this.hoverHandle.cancel();
            }
            this.hoverHandle = tween.to(
                    this.hoverProperty,
                    alpha,
                    0,
                    0.25f,
                    Interpolators.INT,
                    Eases.CUBIC_OUT
            );
        }

        if (alpha != 0) {
            this.renderOp.render(
                    graphics,
                    x, y,
                    control.getWidth(),
                    control.getHeight(),
                    0x00fffee0 | alpha << 24
            );
        }

    }

}
