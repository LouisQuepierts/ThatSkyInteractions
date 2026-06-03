package net.quepierts.thatskyinteractions.feature.client;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.quepierts.thatskyinteractions.feature.interaction.PlayerInteractionAttachment;
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

}
