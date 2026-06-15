package net.quepierts.thatskyinteractions.feature.client.gui.component.sky;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.quepierts.thatskyinteractions.core.transition.BooleanTransition;
import net.quepierts.thatskyinteractions.feature.client.gui.ColorStack;
import net.quepierts.thatskyinteractions.feature.client.gui.component.attribute.AttributeKey;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Button;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Eases;
import org.jspecify.annotations.NonNull;

public class TsiButton extends Button {

    public TsiButton(
            final TweenScope tween,
            final int x, final int y,
            final int width, final int height,
            final Component message
    ) {
        super(tween, x, y, width, height, message);
    }

    @Override
    protected void onMouseEntered() {
        final var manager = Minecraft.getInstance().getSoundManager();
        manager.play(SimpleSoundInstance.forUI(SoundEvents.ITEM_PICKUP, 0.5f, 0.05f));
    }
}
