package net.quepierts.thatskyinteractions.common.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.simpleanimator.core.network.ISync;
import net.quepierts.simpleanimator.core.network.NetworkPackets;
import net.quepierts.thatskyinteractions.common.data.attachment.UserDataAttachment;
import org.jetbrains.annotations.NotNull;

public class UserDataAttachmentSyncPacket implements ISync {
    public static final Type<UserDataAttachmentSyncPacket> TYPE = NetworkPackets.createType(UserDataAttachmentSyncPacket.class);

    private final UserDataAttachment attachment;

    public UserDataAttachmentSyncPacket(ServerPlayer player) {
        this.attachment = UserDataAttachment.getAttachment(player);
    }

    public UserDataAttachmentSyncPacket(FriendlyByteBuf byteBuf) {
        this.attachment = UserDataAttachment.STREAM_CODEC.decode(byteBuf);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void sync() {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null) {
            UserDataAttachment.getAttachment(player).setComponent(this.attachment);
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        UserDataAttachment.STREAM_CODEC.encode(friendlyByteBuf, this.attachment);
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
