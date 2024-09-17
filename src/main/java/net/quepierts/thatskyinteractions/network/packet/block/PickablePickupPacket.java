package net.quepierts.thatskyinteractions.network.packet.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.quepierts.simpleanimator.core.network.IUpdate;
import net.quepierts.simpleanimator.core.network.NetworkPackets;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.block.entity.IPickable;
import net.quepierts.thatskyinteractions.data.TSIUserData;
import net.quepierts.thatskyinteractions.data.TSIUserDataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PickablePickupPacket implements IUpdate {
    public static final Type<PickablePickupPacket> TYPE = NetworkPackets.createType(PickablePickupPacket.class);

    private final IPickable pickable;
    private final UUID uuid;
    private final BlockPos blockPos;

    public PickablePickupPacket(@NotNull IPickable pickable) {
        this.pickable = pickable;
        this.uuid = IPickable.DUMMY_UUID;
        this.blockPos = IPickable.DUMMY_BP;
    }

    public PickablePickupPacket(FriendlyByteBuf byteBuf) {
        this.pickable = IPickable.DUMMY;
        this.uuid = byteBuf.readUUID();
        this.blockPos = byteBuf.readBlockPos();
    }

    @Override
    public void update(ServerPlayer serverPlayer) {
        if (this.uuid == null || this.blockPos == null) {
            return;
        }

        TSIUserDataStorage userDataManager = ThatSkyInteractions.getInstance().getProxy().getUserDataManager();
        TSIUserData userData = userDataManager.getUserData(serverPlayer.getUUID());

        Level level = serverPlayer.level();
        BlockEntity entity = level.getBlockEntity(blockPos);

        if (entity instanceof IPickable iPickable) {
            if (!iPickable.getUUID().equals(this.uuid)) {
                return;
            }

            if (userData.isPickedUp(iPickable)) {
                return;
            }

            userData.pickup(iPickable);
            iPickable.onPickup(serverPlayer);
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(this.pickable.getUUID());
        friendlyByteBuf.writeBlockPos(this.pickable.getBlockPos());
    }

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
