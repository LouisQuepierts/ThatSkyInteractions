package net.quepierts.thatskyinteractions.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.block.ICloud;
import net.quepierts.thatskyinteractions.registry.BlockEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

public class CloudBlockEntity extends BlockEntity implements ICloud {
    private static final String TAG_SIZE = "size";
    private static final String TAG_OFFSET = "offset";
    private final Vector3i offset = new Vector3i(0);
    private final Vector3i size = new Vector3i(16);
    private boolean recompile = true;
    public CloudBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntities.CLOUD.get(), pos, blockState);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(TAG_SIZE)) {
            int[] array = tag.getIntArray(TAG_SIZE);
            if (array.length == 3) {
                this.setSize(array[0], array[1], array[2]);
            }
        }

        if (tag.contains(TAG_OFFSET)) {
            int[] array = tag.getIntArray(TAG_OFFSET);
            if (array.length == 3) {
                this.setOffset(array[0], array[1], array[2]);
            }
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putIntArray(TAG_SIZE, new int[]{this.size.x, this.size.y, this.size.z});
        tag.putIntArray(TAG_OFFSET, new int[]{this.offset.x, this.offset.y, this.offset.z});
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(@NotNull HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putIntArray(TAG_SIZE, new int[]{this.size.x, this.size.y, this.size.z});
        tag.putIntArray(TAG_OFFSET, new int[]{this.offset.x, this.offset.y, this.offset.z});
        return tag;
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider lookupProvider) {
        super.handleUpdateTag(tag, lookupProvider);

        if (tag.contains(TAG_SIZE)) {
            int[] array = tag.getIntArray(TAG_SIZE);
            if (array.length == 3) {
                this.setSize(array[0], array[1], array[2]);
            }
        }

        if (tag.contains(TAG_OFFSET)) {
            int[] array = tag.getIntArray(TAG_OFFSET);
            if (array.length == 3) {
                this.setOffset(array[0], array[1], array[2]);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void setRemoved() {
        ThatSkyInteractions.getInstance().getClient().getCloudRenderer().removeCloud(this);
        super.setRemoved();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(@NotNull Connection net, @NotNull ClientboundBlockEntityDataPacket pkt,@NotNull  HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
    }

    public void setSize(int x, int y, int z) {
        this.size.set(x, y, z);
        this.recompile = true;
    }

    public void setOffset(int x, int y, int z) {
        this.offset.set(x, y, z);
        this.recompile = true;
    }

    public Vector3i getSize() {
        return new Vector3i(this.size);
    }

    public Vector3i getOffset() {
        return new Vector3i(this.offset);
    }

    public boolean shouldRecompile() {
        return this.recompile;
    }

    public void expand(Direction direction, int strength) {
        if (this.level != null) {
            Vec3i normal = direction.getNormal().multiply(strength);
            switch (direction) {
                case UP:
                case EAST:
                case SOUTH: {
                    this.size.add(normal.getX(), normal.getY(), normal.getZ());
                    break;
                }
                case DOWN:
                case WEST:
                case NORTH: {
                    this.size.sub(normal.getX(), normal.getY(), normal.getZ());
                    this.offset.add(normal.getX(), normal.getY(), normal.getZ());
                    break;
                }
            }
            this.recompile = true;
            this.markUpdate();
        }
    }

    public void reduce(Direction direction, int strength) {
        if (this.level != null) {
            if (this.size.lengthSquared() < (long) strength * strength + 8)
                return;

            Vector3i size = new Vector3i(this.size);
            Vec3i normal = direction.getNormal().multiply(strength);
            switch (direction) {
                case UP:
                case EAST:
                case SOUTH: {
                    size.sub(normal.getX(), normal.getY(), normal.getZ());

                    if (size.x < 2 || size.y < 2 || size.z < 2) {
                        return;
                    }
                    break;
                }
                case DOWN:
                case WEST:
                case NORTH: {
                    size.add(normal.getX(), normal.getY(), normal.getZ());

                    if (size.x < 2 || size.y < 2 || size.z < 2) {
                        return;
                    }

                    this.offset.sub(normal.getX(), normal.getY(), normal.getZ());
                    break;
                }
            }
            this.size.set(size);
            this.recompile = true;
            this.markUpdate();
        }
    }

    public void setShouldRecompile(boolean recompile) {
        this.recompile = recompile;
    }

    public void markUpdate() {
        this.setChanged();

        if (this.level != null) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }
}
