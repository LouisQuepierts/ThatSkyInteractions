package net.quepierts.thatskyinteractions.feature.client.gui.component.friendship;

import dev.anvilcraft.lib.v2.rendering.sdf.SdfGraphics;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.core.property.FloatProperty;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.HoverNode;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.VisualNode;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.button.ButtonRenderOps;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.button.SpinButtonNode;
import net.quepierts.thatskyinteractions.infra.animation.tween.Tween;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Eases;
import net.quepierts.thatskyinteractions.infra.animation.tween.interpolate.Interpolators;
import org.joml.Vector2fc;
import org.jspecify.annotations.NonNull;

@UtilityClass
public class FriendshipTreeVisualNode {

    public static VisualNode button(
            final @NonNull Identifier icon
    ) {
        final var content   = SpinButtonNode.of((graphics, x, y, width, height, color) -> {
            graphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    icon,
                    -14,
                    -14,
                    0,
                    0,
                    (int) width - 4,
                    (int) height - 4,
                    32,
                    32,
                    32,
                    32,
                    0xFFFFFFFF
            );
        });

        final var hover     = HoverNode.of(ButtonRenderOps.HOVER);

        return VisualNode.combine(content, hover);
    }

    public static VisualNode line(
            final @NonNull Vector2fc direction
    ) {
        return new Line(direction);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class Line implements VisualNode {

        private final FloatProperty progress    = new FloatProperty(0.0f);
        private final Vector2fc     direction;

        @Override
        public void extractRenderState(
                final @NonNull Control control,
                final @NonNull GuiGraphicsExtractor graphics,
                final @NonNull TweenScope tween,

                final int                           mouseX,
                final int                           mouseY,
                final float                         delta
        ) {

            final var pose      = graphics.pose();
            final var py        = control.getY() + pose.m21;

            final var progress  = this.progress.get();
            final var empty     = progress == 0.0f;
            if (empty) {
                if (py > 64.0f) {
                    this.progress.set(0.0f);
                    Tween.to(
                            this.progress,
                            0.0f,
                            1.0f,
                            1.0f,
                            Interpolators.FLOAT,
                            Eases.QUAD_OUT
                    );
                }
                return;
            }

            final var factor    = control.getHeight() * progress;
            final var px0       = control.getX() + direction.x() * 20.0f;
            final var py0       = control.getY() + direction.y() * 20.0f;
            final var px1       = px0 + direction.x() * factor;
            final var py1       = py0 + direction.y() * factor;

            SdfGraphics.getInstance()
                    .reset()

                    .round(0.5f)
                    .color(0xfffffee0)

                    .light(5.0f)
                    .segment(
                            px0, py0,
                            px1, py1
                    )
                    .draw(graphics);

        }
    }

}
