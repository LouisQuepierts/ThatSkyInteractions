package net.quepierts.thatskyinteractions.feature.client.gui.component.visual;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jspecify.annotations.NonNull;

public interface RenderOp {

    void render(
            @NonNull final GuiGraphicsExtractor graphics,
            float                               x,
            float                               y,
            float                               width,
            float                               height,
            int                                 color
    );

}
