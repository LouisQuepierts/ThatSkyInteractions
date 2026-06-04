package net.quepierts.thatskyinteractions.feature.client.gui.component.visual;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import net.quepierts.thatskyinteractions.infra.animation.tween.Tween;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import org.jspecify.annotations.NonNull;

@FunctionalInterface
public interface VisualNode {

    void extractRenderState(
            @NonNull final Control              control,
            @NonNull final GuiGraphicsExtractor graphics,
            @NonNull final TweenScope           tween,

            final int                           mouseX,
            final int                           mouseY,
            final float                         delta
    );

}
