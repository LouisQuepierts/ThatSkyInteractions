package net.quepierts.thatskyinteractions.network.packet.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.quepierts.simpleanimator.core.network.BiPacket;
import net.quepierts.simpleanimator.core.network.IUpdate;
import net.quepierts.simpleanimator.core.network.NetworkPackets;
import net.quepierts.thatskyinteractions.block.entity.CloudBlockEntity;
import net.quepierts.thatskyinteractions.block.entity.ColoredCloudBlockEntity;
import net.quepierts.thatskyinteractions.registry.DataComponents;
import net.quepierts.thatskyinteractions.registry.Items;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;

import java.util.Optional;

public class UpdateCloudDataPacket implements IUpdate {
    public static final Type<UpdateCloudDataPacket> TYPE = NetworkPackets.createType(UpdateCloudDataPacket.class);

    private final Vec3i position;
    private final Vec3i offset;
    private final Vec3i size;
    private final Vec3i color;

    public UpdateCloudDataPacket(CloudBlockEntity entity) {
        BlockPos pos = entity.getBlockPos();
        this.position = new Vec3i(pos.getX(), pos.getY(), pos.getZ());
        Vector3i offset = entity.getOffset();
        Vector3i size = entity.getSize();
        this.offset = new Vec3i(offset.x, offset.y, offset.z);
        this.size = new Vec3i(size.x, size.y, size.z);

        if (entity instanceof ColoredCloudBlockEntity colored) {
            int color = colored.getColor();
            this.color = new Vec3i(
                    FastColor.ARGB32.red(color),
                    FastColor.ARGB32.green(color),
                    FastColor.ARGB32.blue(color)
            );
        } else {
            this.color = null;
        }
    }

    public UpdateCloudDataPacket(FriendlyByteBuf byteBuf) {
        this.position = this.readVector3i(byteBuf);
        this.offset = this.readVector3i(byteBuf);
        this.size = this.readVector3i(byteBuf);
        this.color = byteBuf.readOptional(this::readVector3i).orElse(null);
    }

    @Override
    public void update(ServerPlayer serverPlayer) {
        if (serverPlayer == null) {
            return;
        }

        if (serverPlayer.isCreative() && serverPlayer.hasPermissions(4)) {
            ItemStack item = serverPlayer.getMainHandItem();

            if (!item.is(Items.CLOUD_EDITOR)) {
                return;
            }

            Vec3i vec3i = item.get(DataComponents.VEC3I);
            if (vec3i == null || !vec3i.equals(this.position)) {
                return;
            }

            Level level = serverPlayer.level();
            BlockPos pos = new BlockPos(this.position);
            BlockEntity entity = level.getBlockEntity(pos);

            if (entity instanceof CloudBlockEntity cloud) {
                cloud.setOffset(this.offset.getX(), this.offset.getY(), this.offset.getZ());
                cloud.setSize(this.size.getX(), this.size.getY(), this.size.getZ());

                if (cloud instanceof ColoredCloudBlockEntity colored && this.color != null) {
                    colored.setColor(FastColor.ARGB32.color(this.color.getX(), color.getY(), color.getZ()));
                }

                cloud.markUpdate();
                BlockState state = level.getBlockState(pos);
                level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
            }
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        this.writeVector3i(friendlyByteBuf, this.position);
        this.writeVector3i(friendlyByteBuf, this.offset);
        this.writeVector3i(friendlyByteBuf, this.size);
        friendlyByteBuf.writeOptional(Optional.ofNullable(this.color), this::writeVector3i);
    }

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private void writeVector3i(FriendlyByteBuf byteBuf, Vec3i vector3i) {
        byteBuf.writeVarInt(vector3i.getX());
        byteBuf.writeVarInt(vector3i.getY());
        byteBuf.writeVarInt(vector3i.getZ());
    }

    private Vec3i readVector3i(FriendlyByteBuf byteBuf) {
        return new Vec3i(
                byteBuf.readVarInt(),
                byteBuf.readVarInt(),
                byteBuf.readVarInt()
        );
    }
}
