package net.quepierts.thatskyinteractions.feature.client.gui.component.friendship;

import dev.anvilcraft.lib.v2.rendering.sdf.SdfGraphics;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.core.property.FloatProperty;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.VisualNode;
import net.quepierts.thatskyinteractions.infra.animation.tween.Tween;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Eases;
import net.quepierts.thatskyinteractions.infra.animation.tween.interpolate.Interpolators;
import org.joml.Vector2fc;
import org.jspecify.annotations.NonNull;

public final class FriendshipTreeLine extends Control {

    public FriendshipTreeLine(final Vector2fc direction) {
        super(0, 0, 2, 0, Component.empty());

        this.setVisualNodes(new Visual(direction));
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Visual implements VisualNode {

        private final FloatProperty progress    = new FloatProperty(0.0f);
        private final Vector2fc     direction;

        @Override
        public void extractRenderState(
                final @NonNull Control control,
                final @NonNull GuiGraphicsExtractor graphics,
                final int mouseX,
                final int mouseY,
                final float delta
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
