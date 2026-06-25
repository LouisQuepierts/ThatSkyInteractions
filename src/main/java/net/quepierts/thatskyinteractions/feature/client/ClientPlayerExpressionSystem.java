package net.quepierts.thatskyinteractions.feature.client;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerBone;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.client.control.event.CameraAlignEvent;
import net.quepierts.thatskyinteractions.feature.client.control.event.LocalPlayerMovedEvent;
import net.quepierts.thatskyinteractions.feature.client.control.event.LocalPlayerTurnEvent;
import net.quepierts.thatskyinteractions.feature.client.gui.ScreenLoader;
import net.quepierts.thatskyinteractions.feature.client.gui.screen.ExpressionsScreen;
import net.quepierts.thatskyinteractions.feature.client.reference.TsiKeys;
import net.quepierts.thatskyinteractions.feature.expression.PlayerExpressionAttachment;
import net.quepierts.thatskyinteractions.feature.expression.PlayerExpressionSystem;
import net.quepierts.thatskyinteractions.feature.expression.packet.ExpressionRequestPacket;
import net.quepierts.thatskyinteractions.feature.expression.runtime.ExpressionState;
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
            final var reference         = attachment.getReference();
            final var expression        = reference.get();

            if (expression              == null) {
                return;
            }

            final var state             = attachment.getState();
            if (expression              .isInterruptible(player, state)) {

                ClientPlayerExpressionSystem.cancel();

            }

            if (expression              .isRestrictMotion(player, state)) {

                event.setCanceled(true);

            }

        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onLocalPlayerTurn(final LocalPlayerTurnEvent event) {
            final var player            = event.getPlayer();

            final var attachment        = PlayerExpressionSystem.getAttachment(player);
            final var reference         = attachment.getReference();
            final var expression        = reference.get();

            if (expression              == null) {
                return;
            }

            final var state             = attachment.getState();

            if (!expression             .isRestrictCamera(player, state)) {
                return;
            }

            final var minecraft         = Minecraft.getInstance();
            final var firstPerson       = minecraft.options
                                        .getCameraType()
                                        .isFirstPerson();

            if (firstPerson) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onCameraAlign(final CameraAlignEvent event) {

            final var player            = event.getEntity();

            final var attachment        = PlayerExpressionSystem.getAttachment(player);
            final var reference         = attachment.getReference();
            final var expression        = reference.get();

            if (expression              == null) {
                return;
            }

            final var state             = attachment.getState();

            if (!expression             .isRestrictCamera(player, state)) {
                return;
            }

            final var minecraft         = Minecraft.getInstance();
            final var firstPerson       = minecraft.options
                    .getCameraType()
                    .isFirstPerson();

            final var partialTick       = event.getPartialTick();
            final var target            = player.getPreciseBodyRotation(partialTick);
            final var current           = player.getYRot(partialTick);

            if (firstPerson) {
                player.setYRot(target);
                player.setYHeadRot(player.getYRot());
            } else {
                final var delta         = Mth.wrapDegrees(current - target);
                final var actual        = (Mth.clamp(delta, -45.0F, 45.0F) - delta);

                player.yRotO += actual;
                player.setYRot(player.getYRot() + actual);
                player.setYHeadRot(player.getYRot());
            }
        }

        @SubscribeEvent
        public static void onPlayerClick(final InputEvent.InteractionKeyMappingTriggered event) {

            final var player            = Minecraft.getInstance().player;

            if (player                  == null) { // just for in case
                return;
            }

            final var attachment        = PlayerExpressionSystem.getAttachment(player);
            final var reference         = attachment.getReference();
            final var expression        = reference.get();

            if (expression              == null) {
                return;
            }

            final var hand              = event.getHand();
            final var state             = attachment.getState();

            if (expression              .isRestrictInput(player, hand, state)) {

                event                   .setCanceled(true);
                event                   .setSwingHand(false);

            }

        }

    }

}
