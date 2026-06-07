package net.quepierts.thatskyinteractions.feature.client.gui.component.floating;

import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.feature.gui.FloatingControlHandle;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

public class FloatingButton extends FloatingControl {

    public static @NonNull FloatingControlConstructor fixed(
            final Component             message,
            final Vector3f              position,
            final Runnable              onClick
    ) {
        return new FloatingControlConstructor() {
            @Override
            protected @NonNull FloatingControl construct(@NonNull final FloatingControlHandle handle, @NonNull final TweenScope tween) {
                return new FloatingButton(
                        tween,
                        message,
                        handle,
                        (dest) -> dest.set(position),
                        onClick
                );
            }
        };
    }

    public static @NonNull FloatingControlConstructor dynamic(
            final Component             message,
            final WorldPositionSupplier supplier,
            final Runnable              onClick
    ) {
        return new FloatingControlConstructor() {
            @Override
            protected @NonNull FloatingControl construct(@NonNull final FloatingControlHandle handle, @NonNull final TweenScope tween) {
                return new FloatingButton(
                        tween,
                        message,
                        handle,
                        supplier,
                        onClick
                );
            }
        };
    }

    private final Runnable onClick;

    protected FloatingButton(
            final TweenScope            tween,
            final Component             message,
            final FloatingControlHandle handle,
            final WorldPositionSupplier worldPosition,
            final Runnable onClick
    ) {
        super(
                tween,
                32, 32,
                message,
                handle,
                worldPosition
        );
        this.onClick = onClick;
    }

}
