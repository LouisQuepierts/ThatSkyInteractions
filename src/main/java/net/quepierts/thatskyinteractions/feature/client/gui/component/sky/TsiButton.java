package net.quepierts.thatskyinteractions.feature.client.gui.component.sky;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Button;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import org.jspecify.annotations.NonNull;

@UtilityClass
public class TsiButton {

    public static final int         BUTTON_SCALE    = 32;
    public static final Runnable    ENTER_SOUND
            = () -> Minecraft.getInstance()
                .getSoundManager()
                .play(SimpleSoundInstance.forUI(SoundEvents.ITEM_PICKUP, 0.5f, 0.05f));

    public static @NonNull Button create(
            final @NonNull TweenScope                   tween,
            final @NonNull Component                    message,
            final          int                          x,
            final          int                          y
    ) {

        final var button = new Button(
                tween,
                x, y,
                BUTTON_SCALE,
                BUTTON_SCALE,
                message
        );

        button.setOnMouseEntered(ENTER_SOUND);

        return button;

    }

}
