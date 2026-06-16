package net.quepierts.thatskyinteractions.feature.client;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.world.InteractionHand;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientChatEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.call.packet.CallRequestPacket;
import net.quepierts.thatskyinteractions.feature.client.reference.TsiKeys;

@UtilityClass
public class ClientPlayerCallSystem {

    public static void call() {

        ClientPacketDistributor.sendToServer(
                CallRequestPacket.request()
        );

    }

    @EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID)
    private static final class Handler {

        @SubscribeEvent
        public static void onPlayerClick(final InputEvent.InteractionKeyMappingTriggered event) {

            if (event.getKeyMapping() != Minecraft.getInstance().options.keyAttack) {
                return;
            }

            if (!TsiKeys.KEY_INTERACT.isDown()) {
                return;
            }

            event.setCanceled(true);
            event.setSwingHand(false);
            ClientPlayerCallSystem.call();

        }

        @SubscribeEvent
        public static void onPlayerChat(final ClientChatEvent event) {

            if (event.getMessage().isEmpty()) {
                return;
            }

            ClientPlayerCallSystem.call();

        }

    }

}
