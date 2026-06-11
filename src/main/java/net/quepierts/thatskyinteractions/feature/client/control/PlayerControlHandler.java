package net.quepierts.thatskyinteractions.feature.client.control;

import lombok.experimental.UtilityClass;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.client.ClientPlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.client.control.event.LocalPlayerMovedEvent;
import net.quepierts.thatskyinteractions.feature.control.PlayerNavigator;

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

        if (controller.shouldRestrictMotion()) {
            event.setCanceled(true);
        }


    }

    @SubscribeEvent
    public static void onPlayerTick(final PlayerTickEvent.Pre event) {
        final var player    = event.getEntity();
        final var navigator = PlayerNavigator.get(player);

        navigator.tick();
    }

}
