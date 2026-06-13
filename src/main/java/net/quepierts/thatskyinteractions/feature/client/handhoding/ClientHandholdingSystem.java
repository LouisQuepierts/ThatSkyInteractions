package net.quepierts.thatskyinteractions.feature.client.handhoding;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.quepierts.thatskyinteractions.feature.handhold.PlayerHandholdingAttachment;
import net.quepierts.thatskyinteractions.feature.handhold.packet.UnholdRequestPacket;

@UtilityClass
public class ClientHandholdingSystem {

    public static void unhold() {
        ClientPacketDistributor.sendToServer(
                UnholdRequestPacket.all()
        );
    }

    public static boolean isFollowing() {
        return getLocalAttachment().getRelation().isFollowing();
    }

    public static boolean isLeading() {
        return getLocalAttachment().getRelation().isLeading();
    }

    public static boolean isHolding() {
        return getLocalAttachment().getRelation().isHolding();
    }

    @SuppressWarnings("DataFlowIssue")
    public static PlayerHandholdingAttachment getLocalAttachment() {
        return PlayerHandholdingAttachment.getAttachment(Minecraft.getInstance().player);
    }

}
