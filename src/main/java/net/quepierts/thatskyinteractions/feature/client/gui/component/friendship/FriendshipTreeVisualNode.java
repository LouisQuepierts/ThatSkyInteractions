package net.quepierts.thatskyinteractions.feature.client.gui.component.friendship;

import dev.anvilcraft.lib.v2.rendering.sdf.SdfGraphics;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.quepierts.thatskyinteractions.core.property.IntProperty;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Button;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.VisualNode;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenHandle;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Eases;
import net.quepierts.thatskyinteractions.infra.animation.tween.interpolate.Interpolators;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor
public class FriendshipTreeVisualNode implements VisualNode {

    private final IntProperty   hoverProperty   = new IntProperty(0);
    private TweenHandle         hoverHandle;
    private boolean             hovering;

    private final Identifier    icon;

    @Override
    public void extractRenderState(
            final @NonNull Control              control,
            final @NonNull GuiGraphicsExtractor graphics,
            final @NonNull TweenScope           tween,

            final int                           mouseX,
            final int                           mouseY,
            final float                         delta
    ) {
        final var x             = control.getX() + 16;
        final var y             = control.getY() + 16;

        SdfGraphics.getInstance()
                .reset()
                .color(0xbbfffee0)
                .center(true)
                .circle(x, y, 0.5f)
                .smooth(20.0f)
                .draw(graphics);

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
            SdfGraphics.getInstance()
                    .reset()
                    .color(0x00fffee0 | alpha << 24)
                    .center(true)
                    .round(5.0f)
                    .box(
                            x, y,
                            control.getWidth() - 2,
                            control.getHeight() - 2
                    )
                    .stroke(1.0f)
                    .light(2.5f)
                    .draw(graphics);
        }

        final var button        = (Button) control;
        final var click         = button.getClickProgress().get();
        final var clicking      = click > 0.0f && click < 1.0f;

        final var pose          = graphics.pose();
        pose                    .pushMatrix();
        pose                    .translate(
                control.getX() + 16,
                control.getY() + 16
        );

        final var t         = Mth.cos(4 * click * Mth.PI);
        pose.scale(t, 1.0f);

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                icon,
                -14,
                -14,
                0,
                0,
                control.getWidth() - 4,
                control.getHeight() - 4,
                32,
                32,
                32,
                32,
                0xFFFFFFFF
        );

        pose                    .popMatrix();
    }
}
