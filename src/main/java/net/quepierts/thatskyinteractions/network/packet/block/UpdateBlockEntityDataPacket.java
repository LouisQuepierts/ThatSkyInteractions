package net.quepierts.thatskyinteractions.network.packet.block;

import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.quepierts.simpleanimator.core.network.IUpdate;
import net.quepierts.simpleanimator.core.network.NetworkPackets;
import net.quepierts.thatskyinteractions.block.entity.AbstractUpdatableBlockEntity;
import org.jetbrains.annotations.NotNull;

public class UpdateBlockEntityDataPacket implements IUpdate {
    public static final Type<UpdateBlockEntityDataPacket> TYPE = NetworkPackets.createType(UpdateBlockEntityDataPacket.class);
    private final BlockPos position;
    private final CompoundTag tag;

    public UpdateBlockEntityDataPacket(AbstractUpdatableBlockEntity entity) {
        this.position = entity.getBlockPos();
        this.tag = new CompoundTag();
        entity.toNBT(this.tag);
    }

    public UpdateBlockEntityDataPacket(FriendlyByteBuf friendlyByteBuf) {
        this.position = friendlyByteBuf.readBlockPos();
        this.tag = friendlyByteBuf.readNbt();
    }

    @Override
    public void update(ServerPlayer serverPlayer) {
        if (serverPlayer == null) {
            return;
        }

        if (serverPlayer.isCreative() && serverPlayer.hasPermissions(Commands.LEVEL_GAMEMASTERS)) {
            ItemStack item = serverPlayer.getMainHandItem();

            Level level = serverPlayer.level();
            BlockPos pos = new BlockPos(this.position);
            BlockEntity entity = level.getBlockEntity(pos);

            if (entity instanceof AbstractUpdatableBlockEntity updatable && updatable.isEditorItem(item)) {
                updatable.fromNBT(tag);
                updatable.markUpdate();
                BlockState state = level.getBlockState(pos);
                level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
            }
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBlockPos(this.position);
        friendlyByteBuf.writeNbt(this.tag);
    }

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
