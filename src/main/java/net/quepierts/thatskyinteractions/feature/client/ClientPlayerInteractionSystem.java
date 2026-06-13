package net.quepierts.thatskyinteractions.feature.client;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.client.control.event.LocalPlayerMovedEvent;
import net.quepierts.thatskyinteractions.feature.interaction.PlayerInteractionAttachment;
import net.quepierts.thatskyinteractions.feature.interaction.PlayerInteractionSystem;
import net.quepierts.thatskyinteractions.feature.interaction.packet.InteractionRequestPacket;

@UtilityClass
@SuppressWarnings({"unused", "DataFlowIssue"})
public class ClientPlayerInteractionSystem {

    public static void invite(
            @NonNull Player         other,
            @NonNull Identifier     interaction
    ) {
        ClientPacketDistributor.sendToServer(
                InteractionRequestPacket.invite(
                        other,
                        interaction
                )
        );
    }

    public static void accept(
            @NonNull Player other
    ) {
        ClientPacketDistributor.sendToServer(
                InteractionRequestPacket.accept(
                        other
                )
        );
    }

    public static void cancel() {
        ClientPacketDistributor.sendToServer(
                InteractionRequestPacket.cancel()
        );
    }

    public static PlayerInteractionAttachment getLocalInteractionData() {
        return PlayerInteractionAttachment.getAttachment(Minecraft.getInstance().player);
    }

    @UtilityClass
    @EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID)
    static final class Handler {
        @SubscribeEvent
        public static void onLocalPlayerMoved(final LocalPlayerMovedEvent event) {

            final var player            = event.getPlayer();

            final var interactionData   = PlayerInteractionSystem.getInteractionAttachment(player);
            final var ongoing           = interactionData.getOngoing();
            if (ongoing != null) {
                if (ongoing.isWaiting()) {
                    ClientPlayerInteractionSystem.cancel();
                }

                event.setCanceled(true);
            }

        }
    }
}
