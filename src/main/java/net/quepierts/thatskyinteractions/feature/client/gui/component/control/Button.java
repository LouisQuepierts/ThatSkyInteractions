package net.quepierts.thatskyinteractions.feature.client.gui.component.control;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.core.property.FloatProperty;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenHandle;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Eases;
import net.quepierts.thatskyinteractions.infra.animation.tween.interpolate.Interpolators;
import org.jspecify.annotations.NonNull;

public class Button extends Control {

    @Getter
    private final FloatProperty clickProgress   = new FloatProperty();
    private TweenHandle         clickHandle;

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

        if (this.clickHandle != null) {
            this.clickHandle.cancel();
        }
        this.clickHandle = this.tween().to(
                this.clickProgress,
                0.0f,
                1.0f,
                0.5f,
                Interpolators.FLOAT,
                Eases.LINEAR
        );

        if (this.onClick != null) {
            this.onClick.run();
        }
    }
}
