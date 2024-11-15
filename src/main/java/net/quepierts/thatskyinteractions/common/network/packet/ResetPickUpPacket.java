package net.quepierts.thatskyinteractions.common.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.quepierts.simpleanimator.core.network.ISync;
import net.quepierts.simpleanimator.core.network.NetworkPackets;
import net.quepierts.thatskyinteractions.common.data.attachment.UserDataAttachment;
import net.quepierts.thatskyinteractions.common.data.attachment.component.PickupComponent;
import org.jetbrains.annotations.NotNull;

public class ResetPickUpPacket implements ISync {
    public static final Type<ResetPickUpPacket> TYPE = NetworkPackets.createType(ResetPickUpPacket.class);

    private final boolean refreshable;

    public ResetPickUpPacket(FriendlyByteBuf byteBuf) {
        this.refreshable = byteBuf.readBoolean();
    }

    public ResetPickUpPacket(boolean refreshable) {
        this.refreshable = refreshable;
    }

    @Override
    public void sync() {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null) {
            return;
        }

        UserDataAttachment attachment = UserDataAttachment.getAttachment(player);
        PickupComponent pickupComponent = attachment.getPickup();

        pickupComponent.unclaim(this.refreshable);
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBoolean(this.refreshable);
    }

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
