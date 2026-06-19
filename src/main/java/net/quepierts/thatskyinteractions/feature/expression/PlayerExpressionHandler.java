package net.quepierts.thatskyinteractions.feature.expression;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;

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
        final var currentId     = attachment.getCurrent();

        if (currentId           == null) {
            return;
        }

        final var manager       = PlayerExpressionManager.getInstance();
        final var expression    = manager.get(currentId);

        if (expression == null
                || expression.immediate()) {
            return;
        }

        final var finished      = expression.isFinished(player);
        if (finished) {
            PlayerExpressionSystem.finish(player);
        }
    }
}
