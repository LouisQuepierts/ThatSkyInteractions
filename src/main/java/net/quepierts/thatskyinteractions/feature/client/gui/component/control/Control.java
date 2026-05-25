package net.quepierts.thatskyinteractions.feature.client.gui.component.control;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.VisualNode;
import net.quepierts.thatskyinteractions.core.model.ui.Insets;
import org.jspecify.annotations.NonNull;

public class Control extends AbstractWidget {

    @Getter
    private final Insets padding;

    @Getter
    private final Insets margin;

    @Setter
    private VisualNode   visualNodes;

    public Control(
            final int       x,
            final int       y,
            final int       width,
            final int       height,
            final Component message
    ) {
        super(x, y, width, height, message);

        this.padding    = new Insets(0);
        this.margin     = new Insets(0);
    }

    @Override
    protected void extractWidgetRenderState(
            final @NonNull GuiGraphicsExtractor graphics,
            final int mouseX,
            final int mouseY,
            final float delta
    ) {
        if (this.visualNodes != null) {
            this.visualNodes.extractRenderState(this, graphics, mouseX, mouseY, delta);
        }
    }

    @Override
    protected void updateWidgetNarration(
            final @NonNull NarrationElementOutput output
    ) {
        output.add(NarratedElementType.TITLE, this.getMessage());
    }
}
