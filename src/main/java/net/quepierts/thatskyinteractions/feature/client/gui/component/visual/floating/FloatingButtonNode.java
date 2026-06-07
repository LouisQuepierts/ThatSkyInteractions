package net.quepierts.thatskyinteractions.feature.client.gui.component.visual.floating;

import dev.anvilcraft.lib.v2.rendering.sdf.SdfGraphics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.VisualNode;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.NonNull;

public final class FloatingButtonNode implements VisualNode {

    public static final Identifier ICON         = ThatSkyInteractions.location("textures/gui/floating.png");

    @Override
    public void extractRenderState(
            @NonNull final Control              control,
            @NonNull final GuiGraphicsExtractor graphics,
            @NonNull final TweenScope           tween,

            final int                           mouseX,
            final int                           mouseY,
            final float                         delta
    ) {

        /*final var radius = control.getWidth() / 2f;
        SdfGraphics.getInstance()
                .reset()
                .center(true)
                .circle(control.x(), control.y(), radius)
                .color(0xb0000000)
                .fill()
                .draw(graphics)

                .color(0xfffffee0)
                .light(2.5f)
                .stroke(0.5f)
                .circle(control.x(), control.y(), radius - 1)
                .draw(graphics);*/

        final var pose = graphics.pose();
        pose.translate(control.x(), control.y());
        pose.translate(0, 8);
        pose.rotate(Mth.HALF_PI * 0.5f);

        graphics.fill(
                0, 0, 8, 8,
                0x80000000
        );

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                ICON,
                0, 0,
                0, 0,
                8, 8,
                8, 8,
                8, 8,
                0xFFFFFFFF
        );

    }

}
