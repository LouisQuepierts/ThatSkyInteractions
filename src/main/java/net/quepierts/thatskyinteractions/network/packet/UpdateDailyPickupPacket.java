package net.quepierts.thatskyinteractions.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.simpleanimator.core.network.ISync;
import net.quepierts.simpleanimator.core.network.NetworkPackets;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.data.TSIUserData;
import org.jetbrains.annotations.NotNull;

public class UpdateDailyPickupPacket implements ISync {
    public static final Type<UpdateDailyPickupPacket> TYPE = NetworkPackets.createType(UpdateDailyPickupPacket.class);
    private final long inGameDay;

    public UpdateDailyPickupPacket(long inGameDay) {
        this.inGameDay = inGameDay;
    }

    public UpdateDailyPickupPacket(FriendlyByteBuf byteBuf) {
        this.inGameDay = byteBuf.readLong();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void sync() {
        TSIUserData userData = ThatSkyInteractions.getInstance().getClient().getCache().getUserData();
        if (userData != null) {
            userData.tryUpdateDaily(this.inGameDay);
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeLong(inGameDay);
    }

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
