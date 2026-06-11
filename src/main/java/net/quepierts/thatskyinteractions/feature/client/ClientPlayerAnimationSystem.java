package net.quepierts.thatskyinteractions.feature.client;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerBone;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationAttachment;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.animation.packet.AnimationRequestPacket;
import net.quepierts.thatskyinteractions.feature.client.control.event.LocalPlayerTurnEvent;
import org.jspecify.annotations.NonNull;

@UtilityClass
@SuppressWarnings({"unused", "DataFlowIssue"})
public class ClientPlayerAnimationSystem {

    public static void play(@NonNull Identifier animation) {

        ClientPacketDistributor.sendToServer(
                AnimationRequestPacket.play(
                        animation
                )
        );
    }

    public static void abort() {
        ClientPacketDistributor.sendToServer(
                AnimationRequestPacket.abort()
        );
    }

    public static void exit() {
        ClientPacketDistributor.sendToServer(
                AnimationRequestPacket.exit()
        );
    }

    public static void event(@NonNull String event) {
        ClientPacketDistributor.sendToServer(
                AnimationRequestPacket.event(
                        event
                )
        );
    }

    public static PlayerAnimationAttachment getLocalAnimationData() {
        return PlayerAnimationAttachment.getAttachment(Minecraft.getInstance().player);
    }


    @UtilityClass
    @EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID)
    static final class Handler {

        @SubscribeEvent
        public static void onLocalPlayerTurn(final LocalPlayerTurnEvent event) {
            final var player        = event.getPlayer();
            final var xo            = event.getXo();

            final var data          = PlayerAnimationSystem.getAnimationData(player);
            final var controller    = data.getController();

            if (!controller.isPlaying()) {
                return;
            }

            final var minecraft     = Minecraft.getInstance();

            final var unlock        = controller.isUnlocked(PlayerBone.HEAD);
            final var firstPerson   = minecraft.options.getCameraType().isFirstPerson();

            if (unlock) {
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

    }

}
