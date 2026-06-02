package net.quepierts.thatskyinteractions.feature.client.control;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerBone;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.client.ClientPlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.client.ClientPlayerInteractionSystem;
import net.quepierts.thatskyinteractions.feature.client.control.event.LocalPlayerMovedEvent;
import net.quepierts.thatskyinteractions.feature.client.control.event.LocalPlayerTurnEvent;
import net.quepierts.thatskyinteractions.feature.control.packet.UpdatePlayerBodyPacket;
import net.quepierts.thatskyinteractions.feature.interaction.PlayerInteractionSystem;

@UtilityClass
@EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID)
public class PlayerControlHandler {

    @SubscribeEvent
    public static void onLocalPlayerMoved(final LocalPlayerMovedEvent event) {
        final var player            = event.getPlayer();

        final var interactionData   = PlayerInteractionSystem.getInteractionData(player);
        final var sent              = interactionData.getSent();
        if (sent != null) {
            if (sent.isWaiting()) {
                ClientPlayerInteractionSystem.cancel();
            }

            event.setCanceled(true);
            return;
        }

        final var animationData     = ClientPlayerAnimationSystem.getLocalAnimationData();
        final var controller        = animationData.getController();

        if (controller.isPlaying()) {
            final var definition    = controller.getDefinition();
            final var fsmState      = controller.getFsmState();
            final var animation     = controller.getAnimation();

            if (definition.abortable() || animation.isLooping(fsmState)) {
                animation.exit(fsmState);
            }

            if (definition.restrictMotion()) {
                event.setCanceled(true);
            }
        }


    }

    @SubscribeEvent
    public static void onLocalPlayerTurn(final LocalPlayerTurnEvent event) {
        final var player        = event.getPlayer();
        final var xo            = event.getXo();

        final var data          = PlayerAnimationSystem.getAnimationData(player);
        final var controller    = data.getController();

        PlayerControlHandler    .update(player);

        if (!controller.isPlaying()) {
            return;
        }

        final var definition    = controller.getDefinition();
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

    private static void update(final LocalPlayer player) {
        if (player.level().players().size() < 2) {
            return;
        }

        ClientPacketDistributor.sendToServer(
                UpdatePlayerBodyPacket.client(player)
        );
    }

}
