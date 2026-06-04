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
import net.quepierts.thatskyinteractions.infra.animation.tween.Tween;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import org.joml.Vector2fc;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class Control extends AbstractWidget {


    private final TweenScope            tween;

    @Getter
    private final Consumer<Vector2fc>   position;

    @Getter
    private final Consumer<Vector2fc>   size;

    @Getter
    private final Insets                padding;

    @Getter
    private final Insets                margin;

    @Setter
    private VisualNode                  visualNodes;

    public Control(
            final TweenScope    tween,
            final int           x,
            final int           y,
            final int           width,
            final int           height,
            final Component     message
    ) {
        super(x, y, width, height, message);

        this.tween      = tween;

        this.position   = vec2 -> this.setPosition((int) vec2.x(), (int) vec2.y());
        this.size       = vec2 -> this.setSize((int) vec2.x(), (int) vec2.y());

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
            this.visualNodes.extractRenderState(
                    this,
                    graphics,
                    this.tween,
                    mouseX,
                    mouseY,
                    delta
            );
        }
    }

    @Override
    protected void updateWidgetNarration(
            final @NonNull NarrationElementOutput output
    ) {
        output.add(NarratedElementType.TITLE, this.getMessage());
    }

    protected boolean areCoordinatesInRectangle(double x, double y) {
        return x >= (double)this.getX() && y >= (double)this.getY() && x < (double)this.getRight() && y < (double)this.getBottom();
    }

    protected TweenScope tween() {
        return this.tween;
    }
}
