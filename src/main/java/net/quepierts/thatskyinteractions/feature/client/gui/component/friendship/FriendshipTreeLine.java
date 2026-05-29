package net.quepierts.thatskyinteractions.feature.client.gui.component.friendship;

import dev.anvilcraft.lib.v2.rendering.sdf.SdfGraphics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.core.property.FloatProperty;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.VisualNode;
import net.quepierts.thatskyinteractions.infra.animation.tween.Tween;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Eases;
import net.quepierts.thatskyinteractions.infra.animation.tween.interpolate.Interpolators;
import org.joml.Matrix3x2fStack;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

public final class FriendshipTreeLine extends Control {

    private final float         degree;

    public FriendshipTreeLine(final float degree) {
        super(0, 0, 2, 0, Component.empty());
        this.degree = degree;

        this.setVisualNodes(new Visual());
    }

    private final class Visual implements VisualNode {

        private final FloatProperty progress = new FloatProperty(0.0f);

        @Override
        public void extractRenderState(
                final @NonNull Control control,
                final @NonNull GuiGraphicsExtractor graphics,
                final int mouseX,
                final int mouseY,
                final float delta
        ) {

            final var pose  = graphics.pose();
            final var point = pose.transform(new Vector3f(control.getX(), control.getY(), 0));

            if (point.y() < 0.0f) {
                return;
            }

            final var progress = this.progress.get();
            if (progress == 0.0f) {
                this.progress.set(1e-6f);
                Tween.to(
                        this.progress,
                        1e-6f,
                        1.0f,
                        1.0f,
                        Interpolators.FLOAT,
                        Eases.QUAD_IN
                );
            }

            SdfGraphics.getInstance()
                    .reset()
                    .box(
                            control.getX() - 0.5f,
                            control.getY(),
                            1.0f,
                            control.getHeight() * progress
                    )
                    .round(0.5f)
                    .light(9.43f)
                    .color(0xfffffee0)
                    .rotate(FriendshipTreeLine.this.degree)
                    .draw(graphics);

        }
    }
}
