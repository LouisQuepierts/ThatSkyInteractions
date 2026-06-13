package net.quepierts.thatskyinteractions.feature.client.gui.component.floating;

import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.quepierts.thatskyinteractions.core.property.BooleanProperty;
import net.quepierts.thatskyinteractions.core.property.FloatProperty;
import net.quepierts.thatskyinteractions.core.property.Vector2fProperty;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenHandle;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Ease;
import net.quepierts.thatskyinteractions.infra.animation.tween.interpolate.Interpolators;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

public class FloatingControl extends Control {

    @Getter
    private final Vector2fProperty      positionProperty    = new Vector2fProperty();

    @Getter
    private final BooleanProperty       restrictPosition    = new BooleanProperty(false);

    @Getter
    private final FloatProperty         showDistance        = new FloatProperty(16);

    @Getter
    private final FloatingTarget        target;

    private final WorldPositionSupplier worldPosition;

    private boolean                     initialized = false;

    @Getter
    private boolean                     removed;

    private float                       tx;
    private float                       ty;
    private TweenHandle                 positionTween;

    public static @NonNull FloatingControlConstructor fixed(
            final int                   width,
            final int                   height,
            final Component             message,
            final Vector3f              position
    ) {
        return new FloatingControlConstructor() {
            @Override
            protected @NonNull FloatingControl construct(@NonNull final FloatingTarget target, @NonNull final TweenScope tween) {
                return new FloatingControl(
                        tween,
                        width,
                        height,
                        message,
                        target,
                        (dest) -> dest.set(position)
                );
            }
        };
    }

    public static @NonNull FloatingControlConstructor dynamic(
            final int                   width,
            final int                   height,
            final Component             message,
            final WorldPositionSupplier supplier
    ) {
        return new FloatingControlConstructor() {
            @Override
            protected @NonNull FloatingControl construct(@NonNull final FloatingTarget target, @NonNull final TweenScope tween) {
                return new FloatingControl(
                        tween,
                        width,
                        height,
                        message,
                        target,
                        supplier
                );
            }
        };
    }

    protected FloatingControl(
            final TweenScope            tween,
            final int                   width,
            final int                   height,
            final Component             message,
            final FloatingTarget target,
            final WorldPositionSupplier worldPosition
    ) {
        super(tween, 0, 0, width, height, message);
        this.target = target;
        this.worldPosition = worldPosition;
    }

    public Vector3f getWorldPosition(
            @NonNull final Vector3f dest
    ) {
        this.worldPosition.get(dest);
        return dest;
    }

    public void markRemoved() {
        this.removed = true;
    }

    public void onRemoved() {
        this.killPositionTween();
    }

    public void onInteract() {

    }

    public float distanceTo(float x, float y) {
        return Float.POSITIVE_INFINITY;
    }

    @Override
    public float x() {
        return this.getPositionProperty().x();
    }

    @Override
    public float y() {
        return this.getPositionProperty().y();
    }

    public boolean isRestrictPosition() {
        return this.restrictPosition.get();
    }

    public void updatePosition(
            final float x,
            final float y
    ) {
        this.killPositionTween();
        this.getPositionProperty().set(x, y);
        this.tx = x;
        this.ty = y;
    }

    public void toPosition(
            final float x,
            final float y
    ) {

        if (!this.initialized) {
            this.updatePosition(x, y);
            this.initialized = true;
        }

        // check distance first
        if (this.tx == x && this.ty == y) {
            return;
        }

        final var distance = this.getPositionProperty().distance(x, y);
        Ease ease = t -> Mth.sin(t * Mth.HALF_PI);

        this.killPositionTween();
        this.positionTween = this.tween().to(
                this.getPositionProperty(),
                new Vector2f(this.getPositionProperty()),
                new Vector2f(x, y),
                Math.clamp(distance * 0.01f, 0.1f, 0.2f),
                Interpolators.FLOAT2,
                ease
        );
    }

    private void killPositionTween() {
        if (this.positionTween != null) {
            this.positionTween.cancel();
            this.positionTween = null;
        }
    }

}
