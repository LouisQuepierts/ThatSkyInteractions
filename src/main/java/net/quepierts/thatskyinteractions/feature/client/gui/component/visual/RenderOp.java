package net.quepierts.thatskyinteractions.feature.client.gui.component.visual;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.quepierts.thatskyinteractions.feature.client.gui.ColorStack;
import org.jspecify.annotations.NonNull;

public interface RenderOp {

    void render(
            @NonNull final GuiGraphicsExtractor graphics,
            @NonNull final ColorStack           colors,
            float                               x,
            float                               y,
            float                               width,
            float                               height
    );

}
