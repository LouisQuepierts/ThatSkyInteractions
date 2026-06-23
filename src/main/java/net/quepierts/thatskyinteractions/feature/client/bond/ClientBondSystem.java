package net.quepierts.thatskyinteractions.feature.client.bond;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.quepierts.thatskyinteractions.feature.bond.PlayerBondAttachment;
import net.quepierts.thatskyinteractions.feature.bond.packet.UnholdRequestPacket;

@UtilityClass
public class ClientBondSystem {

    public static void unhold() {
        ClientPacketDistributor.sendToServer(
                UnholdRequestPacket.all()
        );
    }

    public static void unhold(
            final @NonNull Player other
    ) {
        ClientPacketDistributor.sendToServer(
                UnholdRequestPacket.other(other)
        );
    }

    public static boolean isFollowing() {
        return getLocalAttachment().getHandhold().isFollowing();
    }

    public static boolean isLeading() {
        return getLocalAttachment().getHandhold().isLeading();
    }

    public static boolean isHolding() {
        return getLocalAttachment().getHandhold().isHolding();
    }

    @SuppressWarnings("DataFlowIssue")
    public static PlayerBondAttachment getLocalAttachment() {
        return PlayerBondAttachment.getAttachment(Minecraft.getInstance().player);
    }

}
