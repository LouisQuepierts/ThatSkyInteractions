package net.quepierts.thatskyinteractions.feature.client;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientChatEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.expression.PlayerPreferenceAttachment;
import net.quepierts.thatskyinteractions.feature.expression.call.PlayerVoiceType;
import net.quepierts.thatskyinteractions.feature.expression.call.packet.CallRequestPacket;
import net.quepierts.thatskyinteractions.feature.client.reference.TsiKeys;

@UtilityClass
@SuppressWarnings("DataFlowIssue")
public class ClientPlayerCallSystem {

    public static void call() {

        ClientPacketDistributor.sendToServer(
                CallRequestPacket.request()
        );

    }

    public static boolean hasVoice() {
        final var attachment = PlayerPreferenceAttachment.getAttachment(Minecraft.getInstance().player);
        return !attachment.getVoice().equals(PlayerVoiceType.DEFAULT_ID);
    }

    public static @NonNull PlayerVoiceType getPreferredVoice() {
        final var attachment = PlayerPreferenceAttachment.getAttachment(Minecraft.getInstance().player);
        return attachment.getVoiceType();
    }

    public PlayerPreferenceAttachment getPreference() {
        return PlayerPreferenceAttachment.getAttachment(Minecraft.getInstance().player);
    }

    @EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID)
    private static final class Handler {

        private static boolean  press0      = false;
        private static boolean  press       = false;

        @SubscribeEvent
        public static void onPlayerClick(final InputEvent.InteractionKeyMappingTriggered event) {

            press = false;
            if (event.getKeyMapping() != Minecraft.getInstance().options.keyAttack) {
                return;
            }

            if (!TsiKeys.KEY_INTERACT.isDown()) {
                return;
            }

            if (!ClientPlayerCallSystem.hasVoice()) {
                return;
            }

            event.setCanceled(true);
            event.setSwingHand(false);
            press = true;

        }

        @SubscribeEvent
        public static void onPlayerTick(final ClientTickEvent.Pre event) {

            if (Minecraft.getInstance().level == null) {
                return;
            }

            if (press0 && !press) {
                ClientPlayerCallSystem.call();
            }

            press0 = press;
            press = false;

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
