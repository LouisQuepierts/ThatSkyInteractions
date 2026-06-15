package net.quepierts.thatskyinteractions.feature.client.gui.component.control;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.core.property.EnumProperty;
import net.quepierts.thatskyinteractions.core.property.FloatProperty;
import net.quepierts.thatskyinteractions.core.property.PropertyEnum;
import net.quepierts.thatskyinteractions.core.transition.BooleanTransition;
import net.quepierts.thatskyinteractions.feature.client.gui.component.attribute.AttributeKey;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenHandle;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Eases;
import net.quepierts.thatskyinteractions.infra.animation.tween.interpolate.Interpolators;
import org.jspecify.annotations.NonNull;

public class Button extends Control {

    public static final AttributeKey<BooleanTransition> ATTRIBUTE_PRESS_TRANSITION
            = new AttributeKey<>("press_transition");
    public static final AttributeKey<FloatProperty>     ATTRIBUTE_PRESS_DURATION
            = new AttributeKey<>("press_duration");
    public static final AttributeKey<BooleanTransition> ATTRIBUTE_CLICK_TRANSITION
            = new AttributeKey<>("click_transition");
    public static final AttributeKey<FloatProperty>     ATTRIBUTE_CLICK_DURATION
            = new AttributeKey<>("click_duration");

    @Getter
    private final FloatProperty                     pressDuration       = new FloatProperty(0.1f);

    @Getter
    private final BooleanTransition                 pressTransition     = new BooleanTransition(
                                                                                Eases.LINEAR,
                                                                                this.pressDuration
                                                                        );

    @Getter
    private final FloatProperty                     clickDuration       = new FloatProperty(0.5f);

    @Getter
    private final BooleanTransition                 clickTransition     = new BooleanTransition(
                                                                                Eases.LINEAR,
                                                                                this.clickDuration
                                                                        );

    @Getter
    private final EnumProperty<ActivationTrigger>   activationTrigger   = new EnumProperty<>(ActivationTrigger.CLICKED);

    @Setter
    private Runnable onClick;

    @Getter
    private boolean pressed;

    public Button(
            final TweenScope    tween,
            final int           x,
            final int           y,
            final int           width,
            final int           height,
            final Component     message
    ) {
        super(tween, x, y, width, height, message);

        this.setAttribute(ATTRIBUTE_PRESS_TRANSITION, this.pressTransition);
        this.setAttribute(ATTRIBUTE_PRESS_DURATION, this.pressDuration);
        this.setAttribute(ATTRIBUTE_CLICK_TRANSITION, this.clickTransition);
        this.setAttribute(ATTRIBUTE_CLICK_DURATION, this.clickDuration);

    }

    @Override
    public void onClick(
            final @NonNull MouseButtonEvent event,
            final boolean                   doubleClick
    ) {
        this.pressTransition.update(this.tween(), true);
        this.pressed = true;

        final var trigger = this.activationTrigger.get();

        if (trigger == ActivationTrigger.CLICKED && !doubleClick
                || trigger == ActivationTrigger.DOUBLE_CLICKED && doubleClick) {

            this.click();

        }
    }

    @Override
    public void onRelease(final @NonNull MouseButtonEvent event) {
        this.pressTransition.update(this.tween(), false);
        this.pressed = false;

        final var trigger = this.activationTrigger.get();

        if (trigger == ActivationTrigger.RELEASED) {
            this.click();
        }

    }

    @Override
    protected void onMouseExited() {
        super.onMouseExited();

        this.pressTransition.update(this.tween(), false);
        this.pressed = false;
    }

    @Override
    public void playDownSound(final @NonNull SoundManager soundManager) { }

    public void setClickDuration(final float duration) {
        this.clickDuration.set(duration);
    }

    public void setActivationTrigger(final ActivationTrigger trigger) {
        this.activationTrigger.set(trigger);
    }

    protected void click() {
        this.clickTransition.set(false);
        this.clickTransition.update(this.tween(), true);

        if (this.onClick != null) {
            this.onClick.run();
        }
    }

    public enum ActivationTrigger implements PropertyEnum<ActivationTrigger> {
        CLICKED,
        DOUBLE_CLICKED,
        RELEASED;

        private static final ActivationTrigger[] VALUES = values();

        @Override
        public ActivationTrigger value(final int ordinal) {
            return VALUES[ordinal];
        }
    }
}
