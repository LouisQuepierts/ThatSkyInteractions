package net.quepierts.thatskyinteractions.feature.client.gui.component.floating;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.feature.client.gui.BooleanTransition;
import net.quepierts.thatskyinteractions.feature.client.gui.ColorStack;
import net.quepierts.thatskyinteractions.feature.client.gui.component.attribute.AttributeKey;
import net.quepierts.thatskyinteractions.feature.gui.FloatingControlHandle;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Eases;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

public class FloatingButton extends FloatingControl {

    public static @NonNull FloatingControlConstructor fixed(
            final Component             message,
            final Vector3f              position,
            final InteractCallback      callback
    ) {
        return new FloatingControlConstructor() {
            @Override
            protected @NonNull FloatingControl construct(@NonNull final FloatingControlHandle handle, @NonNull final TweenScope tween) {
                return new FloatingButton(
                        tween,
                        message,
                        handle,
                        (dest) -> dest.set(position),
                        callback
                );
            }
        };
    }

    public static @NonNull FloatingControlConstructor dynamic(
            final Component             message,
            final WorldPositionSupplier supplier,
            final InteractCallback      callback
    ) {
        return new FloatingControlConstructor() {
            @Override
            protected @NonNull FloatingControl construct(@NonNull final FloatingControlHandle handle, @NonNull final TweenScope tween) {
                return new FloatingButton(
                        tween,
                        message,
                        handle,
                        supplier,
                        callback
                );
            }
        };
    }

    public static final AttributeKey<BooleanTransition> ATTRIBUTE_ACTIVE_TRANSITION
            = new AttributeKey<>("active_transition");

    public static final AttributeKey<BooleanTransition> ATTRIBUTE_FOCUS_TRANSITION
            = new AttributeKey<>("focus_transition");

    private final BooleanTransition activeTransition;
    private final BooleanTransition focusTransition;

    private final InteractCallback  callback;

    protected FloatingButton(
            final TweenScope            tween,
            final Component             message,
            final FloatingControlHandle handle,
            final WorldPositionSupplier worldPosition,
            final InteractCallback      callback
    ) {
        super(
                tween,
                32, 32,
                message,
                handle,
                worldPosition
        );

        this.activeTransition   = new BooleanTransition(Eases.CUBIC_OUT, 0.25f);
        this.focusTransition    = new BooleanTransition(Eases.CUBIC_OUT, 0.25f);
        this.callback           = callback;

        this.setAttribute(ATTRIBUTE_ACTIVE_TRANSITION,  this.activeTransition);
        this.setAttribute(ATTRIBUTE_FOCUS_TRANSITION,   this.focusTransition);
    }

    @Override
    protected void extractControlRenderState(
            final @NonNull GuiGraphicsExtractor graphics,
            final @NonNull ColorStack           colors,
            final int                           mouseX,
            final int                           mouseY,
            final float                         delta
    ) {

        this.activeTransition.update(tween(), this.isActive());
        this.focusTransition.update(tween(), this.isFocused());

        super.extractControlRenderState(graphics, colors, mouseX, mouseY, delta);
    }


    @Override
    public float distanceTo(final float x, final float y) {
        return Vector2f.distance(
                x,
                y,
                this.x(),
                this.y() - 14f
        ) - 18f;
    }

    @Override
    public void onInteract() {
        if (this.callback != null) {
            this.callback.run(this, this.tween());
        }
    }

    public interface InteractCallback {
        void run(
                @NonNull final FloatingButton       button,
                @NonNull final TweenScope           scope
        );
    }
}
