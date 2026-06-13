package net.quepierts.thatskyinteractions.feature.client.gui.component.floating;

import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.VisualNode;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

public abstract class FloatingControlConstructor {

    private VisualNode  visual;
    private boolean     restrict        = false;
    private float       showDistance    = 16f;

    @Contract("_, _ -> new")
    protected abstract @NonNull FloatingControl construct(
            @NonNull FloatingTarget  target,
            @NonNull TweenScope      tween
    );

    @Contract("_, _ -> new")
    public @NonNull FloatingControl apply(
            @NonNull FloatingTarget  target,
            @NonNull TweenScope      tween
    ) {
        final var control   = this.construct(target, tween);
        control             .setVisualNode(this.visual);
        control             .getShowDistance()
                            .set(this.showDistance);

        control             .getRestrictPosition()
                            .set(this.restrict);
        return control;
    }

    @Contract("_ -> this")
    public @NonNull FloatingControlConstructor withVisualNode(@NonNull VisualNode visual) {
        this.visual = visual;
        return this;
    }

    @Contract("_ -> this")
    public @NonNull FloatingControlConstructor withRestrict(final boolean restrict) {
        this.restrict = restrict;
        return this;
    }

    @Contract("_ -> this")
    public @NonNull FloatingControlConstructor withShowDistance(final float distance) {
        this.showDistance = distance;
        return this;
    }
}
