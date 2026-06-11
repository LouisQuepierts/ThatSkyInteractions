package net.quepierts.thatskyinteractions.feature.client.control;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerBone;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.client.ClientPlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.client.ClientPlayerInteractionSystem;
import net.quepierts.thatskyinteractions.feature.client.control.event.LocalPlayerMovedEvent;
import net.quepierts.thatskyinteractions.feature.client.control.event.LocalPlayerTurnEvent;
import net.quepierts.thatskyinteractions.feature.control.PlayerNavigator;
import net.quepierts.thatskyinteractions.feature.control.packet.UpdatePlayerBodyPacket;
import net.quepierts.thatskyinteractions.feature.interaction.PlayerInteractionSystem;

@UtilityClass
@EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID)
public class PlayerControlHandler {

    @SubscribeEvent
    public static void onLocalPlayerMoved(final LocalPlayerMovedEvent event) {
        final var player            = event.getPlayer();

        final var navigator         = PlayerNavigator.get(player);
        if (navigator.isNavigating()) {
            navigator.exit();
            event.setCanceled(true);
            return;
        }

        // todo: migrate
        final var animationData     = ClientPlayerAnimationSystem.getLocalAnimationData();
        final var controller        = animationData.getController();

        if (controller.isPlaying()) {
            /*final var current       = controller.getCurrent();
            final var definition    = current.getDefinition();
            final var fsmState      = current.getFsmState();
            final var animation     = current.getAnimation();*/

            /*if (definition.abortable() || animation.isLooping(fsmState)) {
                ClientPlayerAnimationSystem.exit();
            }*/

            if (controller.shouldRestrictMotion()) {
                event.setCanceled(true);
            }
        }


    }

    @SubscribeEvent
    public static void onPlayerTick(final PlayerTickEvent.Pre event) {
        final var player    = event.getEntity();
        final var navigator = PlayerNavigator.get(player);

        navigator.tick();
    }

    /*@SubscribeEvent
    public static void onPlayerTick(final PlayerTickEvent.Pre event) {
        final var player    = event.getEntity();
        final var navigator = PlayerNavigator.get(player);

        navigator.tick();
    }*/

    private static void update(final LocalPlayer player) {
        if (player.level().players().size() < 2) {
            return;
        }

        /*ClientPacketDistributor.sendToServer(
                UpdatePlayerBodyPacket.client(player)
        );*/
    }

}
