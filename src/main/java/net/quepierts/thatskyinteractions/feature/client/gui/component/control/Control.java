package net.quepierts.thatskyinteractions.feature.client.gui.component.control;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.feature.client.gui.component.attribute.AttributeHolder;
import net.quepierts.thatskyinteractions.feature.client.gui.component.attribute.IAttributeHolder;
import net.quepierts.thatskyinteractions.feature.client.gui.component.attribute.AttributeKey;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.VisualNode;
import net.quepierts.thatskyinteractions.core.model.ui.Insets;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import org.joml.Vector2fc;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class Control extends AbstractWidget implements IAttributeHolder {

    private final AttributeHolder       attributes  = new AttributeHolder();
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
    @Getter(AccessLevel.PROTECTED)
    private VisualNode                  visualNode;

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

        if (this.visualNode != null) {
            final var pose = graphics.pose();
            pose.pushMatrix();
            this.visualNode.extractRenderState(
                    this,
                    graphics,
                    this.tween,
                    mouseX,
                    mouseY,
                    delta
            );
            pose.popMatrix();
        }
    }

    public float x() {
        return this.getX();
    }

    public float y() {
        return this.getY();
    }

    @Override
    protected void updateWidgetNarration(
            final @NonNull NarrationElementOutput output
    ) {
        output.add(NarratedElementType.TITLE, this.getMessage());
    }

    protected TweenScope tween() {
        return this.tween;
    }

    @Override
    public <T> void setAttribute(
            final @NonNull AttributeKey<T>  key,
            @NonNull final T                value
    ) {
        this.attributes.setAttribute(key, value);
    }

    @Override
    public <T> T getAttribute(
            final @NonNull AttributeKey<T>  key
    ) {
        return this.attributes.getAttribute(key);
    }

    @Override
    public <T> boolean hasAttribute(
            final @NonNull AttributeKey<T>  key
    ) {
        return this.attributes.hasAttribute(key);
    }
}
