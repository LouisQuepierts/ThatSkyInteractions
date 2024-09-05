package net.quepierts.thatskyinteractions.network.packet.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.quepierts.simpleanimator.core.network.IUpdate;
import net.quepierts.simpleanimator.core.network.NetworkPackets;
import net.quepierts.thatskyinteractions.block.entity.CloudBlockEntity;
import net.quepierts.thatskyinteractions.block.entity.ColoredCloudBlockEntity;
import org.joml.Vector3i;

import java.util.Optional;

public class UpdateCloudDataPacket implements IUpdate {
    public static final Type<UpdateCloudDataPacket> TYPE = NetworkPackets.createType(UpdateCloudDataPacket.class);

    private final Vector3i position;
    private final Vector3i offset;
    private final Vector3i size;
    private final Optional<Vector3i> color;

    public UpdateCloudDataPacket(CloudBlockEntity entity) {
        BlockPos pos = entity.getBlockPos();
        this.position = new Vector3i(pos.getX(), pos.getY(), pos.getZ());
        this.offset = entity.getOffset();
        this.size = entity.getSize();

        if (entity instanceof ColoredCloudBlockEntity colored) {
            int color = colored.getColor();
            this.color = Optional.of(new Vector3i(
                    FastColor.ARGB32.red(color),
                    FastColor.ARGB32.green(color),
                    FastColor.ARGB32.blue(color)
            ));
        } else {
            this.color = Optional.empty();
        }
    }

    public UpdateCloudDataPacket(FriendlyByteBuf byteBuf) {
        this.position = this.readVector3i(byteBuf);
        this.offset = this.readVector3i(byteBuf);
        this.size = this.readVector3i(byteBuf);
        this.color = byteBuf.readOptional(this::readVector3i);
    }

    @Override
    public void update(ServerPlayer serverPlayer) {
        if (serverPlayer == null) {
            return;
        }

        if (serverPlayer.isCreative() && serverPlayer.hasPermissions(4)) {
            Level level = serverPlayer.level();

            BlockEntity entity = level.getBlockEntity(new BlockPos(this.position.x, this.position.y, this.position.z));

            if (entity instanceof CloudBlockEntity cloud) {
                cloud.setOffset(this.position.x, this.position.y, this.position.z);
                cloud.setSize(this.size.x, this.size.y, this.size.z);

                if (cloud instanceof ColoredCloudBlockEntity colored && this.color.isPresent()) {
                    Vector3i color = this.color.get();
                    colored.setColor(FastColor.ARGB32.color(color.x, color.y, color.z));
                }

                cloud.markUpdate();
            }
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        this.writeVector3i(friendlyByteBuf, this.position);
        this.writeVector3i(friendlyByteBuf, this.offset);
        this.writeVector3i(friendlyByteBuf, this.size);
        friendlyByteBuf.writeOptional(this.color, this::writeVector3i);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private void writeVector3i(FriendlyByteBuf byteBuf, Vector3i vector3i) {
        byteBuf.writeVarInt(vector3i.x);
        byteBuf.writeVarInt(vector3i.y);
        byteBuf.writeVarInt(vector3i.z);
    }

    private Vector3i readVector3i(FriendlyByteBuf byteBuf) {
        return new Vector3i(
                byteBuf.readVarInt(),
                byteBuf.readVarInt(),
                byteBuf.readVarInt()
        );
    }
}
