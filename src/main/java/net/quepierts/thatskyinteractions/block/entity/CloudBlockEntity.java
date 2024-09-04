package net.quepierts.thatskyinteractions.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.block.ICloud;
import net.quepierts.thatskyinteractions.registry.BlockEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.List;

public abstract class CloudBlockEntity extends BlockEntity implements ICloud {
    private static final String TAG_SIZE = "size";
    private static final String TAG_OFFSET = "offset";
    private final Vector3i offset = new Vector3i(0);
    private final Vector3i size = new Vector3i(16);
    private AABB aabb;
    private boolean recompile = true;

    protected CloudBlockEntity(BlockEntityType<? extends CloudBlockEntity> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.aabb = new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
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

    public void setSize(int x, int y, int z) {
        this.size.set(x, y, z);
        this.recompile = true;
    }

    public void setOffset(int x, int y, int z) {
        this.offset.set(x, y, z);
        this.recompile = true;
    }

    public void recomputeAABB() {
        BlockPos pos = this.getBlockPos();
        float x0 = pos.getX() + this.offset.x() / 16f;
        float y0 = pos.getY() + this.offset.y() / 16f;
        float z0 = pos.getZ() + this.offset.z() / 16f;
        this.aabb = new AABB(
                x0, y0, z0,
                x0 + this.size.x() / 16f,
                y0 + this.size.y() / 16f,
                z0 + this.size.z() / 16f
        );
    }

    public void setAABB(int x1, int y1, int z1, int x2, int y2, int z2) {

    }

    public Vector3i getSize() {
        return new Vector3i(this.size);
    }

    public Vector3f getSizeF() {
        return new Vector3f(this.size);
    }

    public Vector3i getOffset() {
        return new Vector3i(this.offset);
    }

    public Vector3f getOffsetF() {
        return new Vector3f(this.offset);
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
        if (this.recompile) {
            this.recomputeAABB();
        }
        this.recompile = recompile;
    }

    public void markUpdate() {
        this.setChanged();

        if (this.level != null) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState blockState, CloudBlockEntity cloud) {
        if (cloud.aabb == null) {
            return;
        }

        Vector3i size = cloud.size;
        if (size.x < 16 || size.y < 16 || size.z < 16) {
            return;
        }

        List<Entity> entities = level.getEntitiesOfClass(Entity.class, cloud.aabb);
        for (Entity entity : entities) {
            if (entity instanceof Player player && player.getAbilities().flying) {
                continue;
            }
            Vec3 position = entity.position();
            Vec3 movement = entity.getDeltaMovement();
            double absY = position.y - cloud.aabb.maxY + 0.35;
            double sqrY = absY * absY;


            entity.setDeltaMovement(movement.add(
                    0,
                    absY < -0.5 ? 0.2 : Math.max(absY * absY * 0.943, 0.2),
                    0
            ));
            entity.resetFallDistance();
        }
    }

    private static Vec3 clampAABB(Vec3 pos, AABB aabb) {
        return new Vec3(
                Mth.clamp(pos.x, aabb.minX, aabb.maxX),
                Mth.clamp(pos.y, aabb.minY, aabb.maxY),
                Mth.clamp(pos.z, aabb.minZ, aabb.maxZ)
        );
    }
}
