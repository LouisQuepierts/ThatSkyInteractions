package net.quepierts.thatskyinteractions.feature.client.gui.component.floating;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.core.transition.BooleanTransition;
import net.quepierts.thatskyinteractions.feature.client.gui.ColorStack;
import net.quepierts.thatskyinteractions.feature.client.gui.ExtendedGuiGraphics;
import net.quepierts.thatskyinteractions.feature.client.gui.component.attribute.AttributeKey;
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
            protected @NonNull FloatingControl construct(final @NonNull FloatingTarget target, final @NonNull TweenScope tween) {
                return new FloatingButton(
                        tween,
                        message,
                        target,
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
            protected @NonNull FloatingControl construct(final @NonNull FloatingTarget target, final @NonNull TweenScope tween) {
                return new FloatingButton(
                        tween,
                        message,
                        target,
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

    public static final AttributeKey<BooleanTransition> ATTRIBUTE_CLICK_TRANSITION
            = new AttributeKey<>("click_transition");

    private final BooleanTransition activeTransition;
    private final BooleanTransition focusTransition;
    private final BooleanTransition clickTransition;

    private final InteractCallback  callback;

    protected FloatingButton(
            final TweenScope            tween,
            final Component             message,
            final FloatingTarget        target,
            final WorldPositionSupplier worldPosition,
            final InteractCallback      callback
    ) {
        super(
                tween,
                32, 32,
                message,
                target,
                worldPosition
        );

        this.activeTransition   = new BooleanTransition(Eases.CUBIC_OUT, 0.25f);
        this.focusTransition    = new BooleanTransition(Eases.CUBIC_OUT, 0.25f);
        this.clickTransition    = new BooleanTransition(Eases.CUBIC_OUT, 1.0f);
        this.callback           = callback;

        this.setAttribute(ATTRIBUTE_ACTIVE_TRANSITION,  this.activeTransition);
        this.setAttribute(ATTRIBUTE_FOCUS_TRANSITION,   this.focusTransition);
        this.setAttribute(ATTRIBUTE_CLICK_TRANSITION,   this.clickTransition);
    }

    @Override
    protected void extractControlRenderState(
            final @NonNull ExtendedGuiGraphics  graphics,
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
        this.clickTransition.set(false);
        this.clickTransition.update(tween(), true);

        this.playDownSound(Minecraft.getInstance().getSoundManager());

        if (this.callback != null) {
            this.callback.run(this, this.tween());
        }
    }

    public interface InteractCallback {
        void run(
                final @NonNull FloatingButton       button,
                final @NonNull TweenScope           scope
        );
    }
}
