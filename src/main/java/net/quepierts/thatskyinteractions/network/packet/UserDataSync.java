package net.quepierts.thatskyinteractions.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.simpleanimator.core.network.ISync;
import net.quepierts.simpleanimator.core.network.NetworkPackets;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.data.TSIUserData;

public class UserDataSync implements ISync {
    public static final Type<UserDataSync> TYPE = NetworkPackets.createType(UserDataSync.class);
    private final TSIUserData userData;

    public UserDataSync(FriendlyByteBuf byteBuf) {
        this.userData = TSIUserData.fromNetwork(byteBuf);
    }

    public UserDataSync(TSIUserData userData) {
        this.userData = userData;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void sync() {
        ThatSkyInteractions.getInstance().getClient().getCache().handleUpdateUserData(this.userData);
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        TSIUserData.toNetwork(friendlyByteBuf, this.userData);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
