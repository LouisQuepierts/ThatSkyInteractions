package net.quepierts.thatskyinteractions.feature.client;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationData;
import net.quepierts.thatskyinteractions.feature.animation.packet.AnimationRequestPacket;
import net.quepierts.thatskyinteractions.feature.registry.AttachmentTypes;
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

    public static PlayerAnimationData getLocalAnimationData() {
        return Minecraft.getInstance().player.getData(AttachmentTypes.PLAYER_ANIMATION);
    }

}
