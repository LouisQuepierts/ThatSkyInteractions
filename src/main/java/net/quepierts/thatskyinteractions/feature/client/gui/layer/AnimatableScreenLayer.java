package net.quepierts.thatskyinteractions.feature.client.gui.layer;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.quepierts.thatskyinteractions.feature.client.gui.ColorStack;
import net.quepierts.thatskyinteractions.feature.client.gui.screen.AnimatableScreen;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public final class AnimatableScreenLayer {

    public static final AnimatableScreenLayer INSTANCE = new AnimatableScreenLayer();

    private final List<AnimatableScreen> screens = new ArrayList<>();

    private int colorStackCapacity = ColorStack.INITIAL_CAPACITY;

    public void push(@NonNull final AnimatableScreen screen) {
        if (!this.screens.contains(screen)) {
            this.screens.add(screen);
        }
    }

    public void pop(@NonNull final AnimatableScreen screen) {
        this.screens.remove(screen);
    }

    void render(
            @NonNull final GuiGraphicsExtractor graphics,
            @NonNull final DeltaTracker tracker,
            final int mouseX,
            final int mouseY
    ) {
        final var delta         = tracker.getRealtimeDeltaTicks();
        final var iterator      = screens.iterator();

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

            screen.extractAnimatableRenderState(
                    graphics,
                    colors,
                    mouseX,
                    mouseY,
                    delta);
        }

        this.colorStackCapacity = Math.max(this.colorStackCapacity, colors.getDeep());
    }

}
