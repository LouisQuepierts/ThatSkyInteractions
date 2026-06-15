package net.quepierts.thatskyinteractions.feature.client.gui.layer;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.quepierts.thatskyinteractions.feature.client.gui.ColorStack;
import net.quepierts.thatskyinteractions.feature.client.gui.ExtendedGuiGraphics;
import net.quepierts.thatskyinteractions.feature.client.gui.screen.AnimatableScreen;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public final class AnimatableScreenLayer {

    public static final AnimatableScreenLayer INSTANCE = new AnimatableScreenLayer();

    private final List<AnimatableScreen> screens = new ArrayList<>();

    private int colorStackCapacity = ColorStack.INITIAL_CAPACITY;

    public void push(final @NonNull AnimatableScreen screen) {
        if (!this.screens.contains(screen)) {
            this.screens.add(screen);
        }
    }

    public void pop(final @NonNull AnimatableScreen screen) {
        this.screens.remove(screen);
    }

    void render(
            final @NonNull GuiGraphicsExtractor graphics,
            final @NonNull DeltaTracker tracker,
            final int mouseX,
            final int mouseY
    ) {
        final var delta         = tracker.getRealtimeDeltaTicks();
        final var iterator      = screens.iterator();
        final var extended      = new ExtendedGuiGraphics(graphics);

        final var colors        = new ColorStack(this.colorStackCapacity);

        while (iterator.hasNext()) {
            final var screen = iterator.next();

            if (screen.isDiscard()) {
                iterator.remove();
                continue;
            }

            if (screen.isHided() && !screen.isAnimating()) {
                continue;
            }

            screen.tick();
            screen.extractAnimatableRenderState(
                   extended,
                    colors,
                    mouseX,
                    mouseY,
                    delta);
        }

        this.colorStackCapacity = Math.max(this.colorStackCapacity, colors.getDeep());
    }

}
