package net.quepierts.thatskyinteractions.feature.client.gui.component.floating;

import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.VisualNode;
import net.quepierts.thatskyinteractions.feature.gui.FloatingControlHandle;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

public abstract class FloatingControlConstructor {

    private VisualNode visual;

    @Contract("_, _ -> new")
    protected abstract @NonNull FloatingControl construct(
            @NonNull FloatingControlHandle  handle,
            @NonNull TweenScope             tween
    );

    @Contract("_, _ -> new")
    public @NonNull FloatingControl apply(
            @NonNull FloatingControlHandle  handle,
            @NonNull TweenScope             tween
    ) {
        final var control   = this.construct(handle, tween);
        control             .setVisualNode(this.visual);
        return control;
    }

    @Contract("_ -> this")
    public @NonNull FloatingControlConstructor withVisualNode(@NonNull VisualNode visual) {
        this.visual = visual;
        return this;
    }
}
