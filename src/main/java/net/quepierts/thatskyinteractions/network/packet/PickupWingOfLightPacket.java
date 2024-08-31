package net.quepierts.thatskyinteractions.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.quepierts.simpleanimator.core.network.IUpdate;
import net.quepierts.simpleanimator.core.network.NetworkPackets;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.block.entity.WingOfLightBlockEntity;
import net.quepierts.thatskyinteractions.data.TSIUserData;
import net.quepierts.thatskyinteractions.data.TSIUserDataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PickupWingOfLightPacket implements IUpdate {
    public static final Type<PickupWingOfLightPacket> TYPE = NetworkPackets.createType(PickupWingOfLightPacket.class);

    @NotNull
    private final UUID wolUUID;
    public PickupWingOfLightPacket(WingOfLightBlockEntity entity) {
        this.wolUUID = entity.getUUID();
    }

    public PickupWingOfLightPacket(FriendlyByteBuf byteBuf) {
        this.wolUUID = byteBuf.readUUID();
    }

    @Override
    public void update(ServerPlayer serverPlayer) {
        TSIUserDataStorage userDataManager = ThatSkyInteractions.getInstance().getProxy().getUserDataManager();
        TSIUserData userData = userDataManager.getUserData(serverPlayer.getUUID());

        userData.pickupWingOfLight(this.wolUUID);
        serverPlayer.addItem(new ItemStack(Items.RED_CANDLE));
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(this.wolUUID);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
