package net.quepierts.thatskyinteractions.feature.client.control;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.ClientInput;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerBone;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.client.control.event.LocalPlayerMovedEvent;
import net.quepierts.thatskyinteractions.feature.client.control.event.LocalPlayerTurnEvent;
import net.quepierts.thatskyinteractions.feature.interaction.PlayerInteractionData;
import net.quepierts.thatskyinteractions.feature.interaction.PlayerInteractionSystem;
import net.quepierts.thatskyinteractions.feature.mixin.vanilla.client.accessor.ClientInputAccessor;

@UtilityClass
@EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID)
public class PlayerControlHandler {

    @SubscribeEvent
    public static void onLocalPlayerMoved(final LocalPlayerMovedEvent event) {
        final var player        = event.getPlayer();

        final var animationData = PlayerAnimationSystem.getAnimationData(player);
        final var animState     = animationData.getAnimation();

        if (animState.isPlaying()) {
            final var definition    = animState.getDefinition();
            final var fsmState      = animState.getFsmState();
            final var animation     = animState.getAnimation();

            if (definition.abortable() || animation.isLooping(fsmState)) {
                animation.exit(fsmState);
            }

            if (definition.restrictMotion()) {
                event.setCanceled(true);
            }
        }

        final var interactionData   = PlayerInteractionSystem.getInteractionData(player);

    }

    @SubscribeEvent
    public static void onLocalPlayerTurn(final LocalPlayerTurnEvent event) {
        final var player        = event.getPlayer();
        final var xo            = event.getXo();

        final var data          = PlayerAnimationSystem.getAnimationData(player);
        final var animation     = data.getAnimation();

        if (!animation.isPlaying()) {
            return;
        }

        final var definition    = animation.getDefinition();
        final var minecraft     = Minecraft.getInstance();

        final var firstPerson   = minecraft.options.getCameraType().isFirstPerson();
        final var lock          = !definition.unlock().contains(PlayerBone.HEAD);

        if (!lock) {
            return;
        }

        if (firstPerson) {
            event.setCanceled(true);
            return;
        }

        final var deltaY        = (float) xo * 0.15f;
        final var headDiff0     = Mth.abs(Mth.wrapDegrees(player.getYRot() - player.yBodyRot));
        final var headDiff1     = Mth.abs(Mth.wrapDegrees(player.getYRot() - player.yBodyRot - deltaY));
        final var maxDiff       = player.isBlocking() ? 14f : 49f;

        if (headDiff0 > maxDiff && headDiff1 < headDiff0) {
            event.setXo(0.0);
        }
    }

}
