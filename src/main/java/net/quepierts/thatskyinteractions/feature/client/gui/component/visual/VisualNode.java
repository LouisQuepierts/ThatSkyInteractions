package net.quepierts.thatskyinteractions.feature.client.gui.component.visual;

import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.quepierts.thatskyinteractions.feature.client.gui.ColorStack;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import net.quepierts.thatskyinteractions.infra.animation.tween.Tween;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;

@FunctionalInterface
public interface VisualNode {

    VisualNode EMPTY = (_, _, _, _, _, _, _) -> {};

    static VisualNode combine(@NonNull VisualNode... nodes) {

        final var length = nodes.length;
        if (length == 0) {
            return EMPTY;
        } else if (length == 1) {
            return nodes[0];
        }

        final var nonempty = new ArrayList<VisualNode>(length);
        for (final var node : nodes) {
            if (node != EMPTY) {
                nonempty.add(node);
            }
        }

        final var size  = nonempty.size();
        if (size == 0) {
            return EMPTY;
        } else if (size == 1) {
            return nonempty.getFirst();
        } else {
            return Combine.of(nonempty.toArray(VisualNode[]::new));
        }

    }

    static VisualNode flatten(@NonNull VisualNode node) {
        if (!(node instanceof Combine combine)) {
            return node;
        }

        final var children      = new ArrayList<VisualNode>();
        final var stack         = new ObjectArrayList<Combine>();
        stack.push(combine);

        while (!stack.isEmpty()) {
            final var current   = stack.pop();
            for (final var child : current.nodes) {
                if (!(child instanceof Combine nest)) {
                    if (child != EMPTY) {
                        children.add(child);
                    }
                    continue;
                }

                stack.push(nest);
            }
        }

        return Combine.of(children.toArray(VisualNode[]::new));
    }

    void extractRenderState(
            @NonNull final Control              control,
            @NonNull final GuiGraphicsExtractor graphics,
            @NonNull final ColorStack           colors,
            @NonNull final TweenScope           tween,

            final int                           mouseX,
            final int                           mouseY,
            final float                         delta
    );

    @RequiredArgsConstructor(staticName = "of")
    final class Combine implements VisualNode {

        private final @NonNull VisualNode @NonNull[] nodes;

        @Override
        public void extractRenderState(
                @NonNull final Control              control,
                @NonNull final GuiGraphicsExtractor graphics,
                @NonNull final ColorStack           colors,
                @NonNull final TweenScope           tween,

                final int                           mouseX,
                final int                           mouseY,
                final float                         delta
        ) {

            final var pose = graphics.pose();
            for (final var node : nodes) {
                pose.pushMatrix();
                node.extractRenderState(
                        control,
                        graphics,
                        colors,
                        tween,

                        mouseX,
                        mouseY,
                        delta
                );
                pose.popMatrix();
            }
        }

    }

}
