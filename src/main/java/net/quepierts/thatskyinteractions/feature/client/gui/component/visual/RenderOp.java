package net.quepierts.thatskyinteractions.feature.client.gui.component.visual;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.quepierts.thatskyinteractions.feature.client.gui.ColorStack;
import net.quepierts.thatskyinteractions.feature.client.gui.ExtendedGuiGraphics;
import org.jspecify.annotations.NonNull;

public interface RenderOp {

    void render(
            final @NonNull ExtendedGuiGraphics  graphics,
            final @NonNull ColorStack           colors,
            float                               x,
            float                               y,
            float                               width,
            float                               height
    );

}
