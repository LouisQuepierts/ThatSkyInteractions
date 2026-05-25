package net.quepierts.thatskyinteractions.feature.client.gui.component.visual;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import org.jspecify.annotations.NonNull;

@FunctionalInterface
public interface VisualNode {

    void extractRenderState(
            @NonNull final Control              control,
            @NonNull final GuiGraphicsExtractor graphics,

            final int                           mouseX,
            final int                           mouseY,
            final float                         delta
    );

}
