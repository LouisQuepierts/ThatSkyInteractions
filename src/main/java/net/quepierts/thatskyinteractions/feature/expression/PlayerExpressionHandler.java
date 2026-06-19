package net.quepierts.thatskyinteractions.feature.expression;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.animation.event.PlayerAnimationControllerEvent;

@Slf4j
@UtilityClass
@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public class PlayerExpressionHandler {

    @SubscribeEvent
    public static void onPlayerLoggedOut(final PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerExpressionSystem.cancel(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(final PlayerTickEvent.Pre event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        final var attachment    = PlayerExpressionSystem.getAttachment(player);
        if (attachment.getCurrent() == null) {
            return;
        }

        final var expression    = attachment.getReference().get();

        if (expression == null) {
            return;
        }

        final var finished      = expression.isFinished(player);
        if (finished) {
            PlayerExpressionSystem.finish(player);
        }
    }

    @SubscribeEvent
    public static void onAnimationFinished(final PlayerAnimationControllerEvent.Finished event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        final var attachment    = PlayerExpressionSystem.getAttachment(player);

        if (attachment.getCurrent() == null) {
            return;
        }

        final var expression    = attachment.getReference().get();

        if (expression == null) {
            return;
        }

        expression.onAnimationFinished(player, event);

    }
}
