package net.quepierts.thatskyinteractions.common.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.quepierts.simpleanimator.core.network.ISync;
import net.quepierts.simpleanimator.core.network.NetworkPackets;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.common.data.TSIUserData;
import org.jetbrains.annotations.NotNull;

public class ResetPickUpPacket implements ISync {
    public static final Type<ResetPickUpPacket> TYPE = NetworkPackets.createType(ResetPickUpPacket.class);

    private final boolean isStatic;

    public ResetPickUpPacket(FriendlyByteBuf byteBuf) {
        this.isStatic = byteBuf.readBoolean();
    }

    public ResetPickUpPacket(boolean isStatic) {
        this.isStatic = isStatic;
    }

    @Override
    public void sync() {
        TSIUserData data = ThatSkyInteractions.getInstance().getClient().getCache().getUserData();
        if (data != null) {
            if (this.isStatic) {
                data.resetStaticPickUp();
            } else {
                data.resetDailyPickUp();
            }
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBoolean(this.isStatic);
    }

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
