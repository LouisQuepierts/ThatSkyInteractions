package net.quepierts.thatskyinteractions.feature.client.gui.component.control;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.core.property.EnumProperty;
import net.quepierts.thatskyinteractions.core.property.FloatProperty;
import net.quepierts.thatskyinteractions.core.property.PropertyEnum;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenHandle;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Eases;
import net.quepierts.thatskyinteractions.infra.animation.tween.interpolate.Interpolators;
import org.jspecify.annotations.NonNull;

public class Button extends Control {

    @Getter
    private final FloatProperty                     clickProgress       = new FloatProperty();
    private TweenHandle                             clickHandle;

    @Getter
    private final FloatProperty                     clickDuration       = new FloatProperty(0.5f);

    @Getter
    private final EnumProperty<ActivationTrigger>   activationTrigger   = new EnumProperty<>(ActivationTrigger.CLICKED);

    @Setter
    private Runnable onClick;

    public Button(
            final TweenScope    tween,
            final int           x,
            final int           y,
            final int           width,
            final int           height,
            final Component     message
    ) {
        super(tween, x, y, width, height, message);
    }

    @Override
    public void onClick(
            final @NonNull MouseButtonEvent event,
            final boolean doubleClick
    ) {

        final var trigger = this.activationTrigger.get();

        if (trigger == ActivationTrigger.CLICKED && !doubleClick
                || trigger == ActivationTrigger.DOUBLE_CLICKED && doubleClick) {

            this.click();

        }
    }

    @Override
    public void onRelease(final @NonNull MouseButtonEvent event) {

        final var trigger = this.activationTrigger.get();

        if (trigger == ActivationTrigger.RELEASED) {
            this.click();
        }

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
        if (this.clickHandle != null) {
            this.clickHandle.cancel();
        }

        this.clickHandle = this.tween().to(
                this.clickProgress,
                0.0f,
                1.0f,
                this.clickDuration.get(),
                Interpolators.FLOAT,
                Eases.LINEAR
        );

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
