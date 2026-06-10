package net.quepierts.thatskyinteractions.feature.client.handhoding;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.quepierts.thatskyinteractions.feature.handhold.PlayerHandholdingAttachment;

@UtilityClass
public class ClientHandholdingSystem {

    @SuppressWarnings("DataFlowIssue")
    public static PlayerHandholdingAttachment getLocalAttachment() {
        return PlayerHandholdingAttachment.getAttachment(Minecraft.getInstance().player);
    }

}
