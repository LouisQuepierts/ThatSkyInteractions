package net.quepierts.thatskyinteractions.feature.client;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.client.control.event.LocalPlayerMovedEvent;
import net.quepierts.thatskyinteractions.feature.client.gui.ScreenLoader;
import net.quepierts.thatskyinteractions.feature.client.gui.screen.ExpressionsScreen;
import net.quepierts.thatskyinteractions.feature.client.reference.TsiKeys;
import net.quepierts.thatskyinteractions.feature.expression.PlayerExpressionSystem;
import net.quepierts.thatskyinteractions.feature.expression.packet.ExpressionRequestPacket;
import org.jspecify.annotations.NonNull;

@UtilityClass
public class ClientPlayerExpressionSystem {

    public static void perform(
            final @NonNull  Identifier  expressionId,
            final           int         level
    ) {

        ClientPacketDistributor.sendToServer(
                ExpressionRequestPacket.perform(expressionId, level)
        );

    }

    public static void cancel() {

        ClientPacketDistributor.sendToServer(
                ExpressionRequestPacket.cancel()
        );

    }

    @UtilityClass
    @EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID)
    private static final class Handler {

        @SubscribeEvent
        public static void onKey(final InputEvent.Key event) {
            final var minecraft = Minecraft.getInstance();

            if (TsiKeys.KEY_OPEN_EXPRESSION.consumeClick()) {

                if (minecraft.screen == null) {
                    ScreenLoader.open(ExpressionsScreen.class);
                }
            }
        }

        @SubscribeEvent
        public static void onLocalPlayerMoved(final LocalPlayerMovedEvent event) {

            final var player            = event.getPlayer();
            final var attachment        = PlayerExpressionSystem.getAttachment(player);

            if (!attachment.isExpressing()) {
                return;
            }

            final var reference         = attachment.getReference();
            final var expression        = reference.get();

            if (expression == null) {
                return;
            }

            if (expression.isInterruptible(player, attachment.getState())) {
                ClientPlayerExpressionSystem.cancel();
            }

            event.setCanceled(true);

        }

    }

}
