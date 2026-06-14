package net.quepierts.thatskyinteractions.feature.client.gui.component.sky.friendship;

import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Button;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import net.quepierts.thatskyinteractions.feature.client.gui.component.layout.Pane;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import org.jspecify.annotations.NonNull;

import java.util.List;

public final class FriendshipTreeLayout extends Pane {

    public static final int NODE_SIZE               = FriendshipTreeComponentFactory.NODE_SIZE;

    private final List<Control>                     lines;
    private final List<Button>                      buttons;

    public FriendshipTreeLayout(
            final @NonNull TweenScope       tween,
            final FriendshipTreeComponents  components,
            final int                       x,
            final int                       y,
            final int                       width
    ) {
        super(
                tween,
                x, y,
                width, 0,
                Component.translatable("gui.thatskyinteractions.friendship.tree")
        );

        this.lines          = components.lines();
        this.buttons        = components.buttons();

        final var children  = this.getChildren();
        children            .addAll(this.lines);
        children            .addAll(this.buttons);

        final var padding   = this.getPadding();
        padding.top         = 16;
        padding.bottom      = 16;

        this.height         = (int) (components.height() + padding.getTop() + padding.getBottom());
    }

    @Override
    public void fit() {

    }

    @Override
    public void layout() {

        final var center    = width / 2 - NODE_SIZE / 2;
        final var first     = this.buttons.getFirst();

        final var dx        = first.getX() - center;
        final var dy        = first.getY() - this.getContentBottom();

        for (final var child : this.getChildren()) {
            final var x     = child.getX();
            final var y     = child.getY();
            child.setPosition(
                    x - dx,
                    y - dy
            );
        }

    }

    @Override
    public void addChild(final Control child) {
        throw new UnsupportedOperationException("Cannot add child to FriendshipTreeLayout");
    }

    private int getContentBottom() {
        return (int) (this.getY() + this.getHeight() - this.getPadding().getBottom() - NODE_SIZE);
    }
}
