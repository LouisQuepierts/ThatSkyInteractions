package net.quepierts.thatskyinteractions.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.quepierts.thatskyinteractions.block.ICloud;
import net.quepierts.thatskyinteractions.registry.Items;
import net.quepierts.thatskyinteractions.registry.TagKeys;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractCloudBlockEntity extends AbstractUpdatableBlockEntity implements ICloud, IUpdateMark {
    private static final Predicate<Entity> CLOUD_IGNORED = (entity) -> {
        if (entity.isSpectator() || entity.isNoGravity()) {
            return false;
        }

        if (entity instanceof Player player) {
            return !(player.getMainHandItem().is(TagKeys.CLOUD_EDIT) || player.getOffhandItem().is(TagKeys.CLOUD_EDIT));
        }

        return true;
    };
    private static final String TAG_SIZE = "size";
    private static final String TAG_OFFSET = "offset";
    private static final String TAG_COLLISIBLE = "collisible";
    private static final String TAG_VANILLA = "vanilla";
    private final Vector3i offset = new Vector3i(0);
    private final Vector3i size = new Vector3i(16);
    private boolean vanilla;
    private AABB aabb;
    private boolean dirty = true;

    protected AbstractCloudBlockEntity(BlockEntityType<? extends AbstractCloudBlockEntity> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.aabb = new AABB(pos);
    }

    @Override
    public void toNBT(@NotNull CompoundTag tag) {
        tag.putIntArray(TAG_SIZE, new int[]{this.size.x, this.size.y, this.size.z});
        tag.putIntArray(TAG_OFFSET, new int[]{this.offset.x, this.offset.y, this.offset.z});
        tag.putBoolean(TAG_VANILLA, this.vanilla);
    }

    @Override
    public void fromNBT(@NotNull CompoundTag tag) {
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

        if (tag.contains(TAG_VANILLA)) {
            this.vanilla = tag.getBoolean(TAG_VANILLA);
        }
    }

    public void setSize(int x, int y, int z) {
        this.size.set(x, y, z);
        this.dirty = true;
    }

    public void setOffset(int x, int y, int z) {
        this.offset.set(x, y, z);
        this.dirty = true;
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

    public int getColor() {
        return 0xffffffff;
    }

    @Override
    public boolean isDirty() {
        return this.dirty;
    }

    @Override
    public void setDirty(boolean recompile) {
        if (this.dirty) {
            this.recomputeAABB();
        }
        this.dirty = recompile;
    }

    public boolean isVanilla() {
        return vanilla;
    }

    public void setVanilla(boolean vanilla) {
        this.vanilla = vanilla;
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
            this.dirty = true;
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
            this.dirty = true;
            this.markUpdate();
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState blockState, AbstractCloudBlockEntity cloud) {
        if (cloud.aabb == null) {
            return;
        }

        Vector3i size = cloud.size;
        if (size.x < 16 || size.y < 16 || size.z < 16) {
            return;
        }

        List<Entity> entities = level.getEntitiesOfClass(Entity.class, cloud.aabb, CLOUD_IGNORED);

        for (Entity entity : entities) {
            Vec3 position = entity.position();
            Vec3 movement = entity.getDeltaMovement();

            double distance = cloud.aabb.maxY - position.y;
            double oscillationForce = Mth.cos((float) Mth.clamp(distance, 0, 1) * Mth.PI + Mth.PI) * 0.1;

            if (distance > 2) {
                oscillationForce = Math.min(0.5f, oscillationForce + (distance - 1.8) / 8);
            }
            entity.setDeltaMovement(
                    movement.x,
                    movement.y + oscillationForce,
                    movement.z);
        }
    }

    @Override
    public boolean isEditorItem(ItemStack item) {
        return item.is(Items.CLOUD_EDITOR);
    }

    private static Vec3 clampAABB(Vec3 pos, AABB aabb) {
        return new Vec3(
                Mth.clamp(pos.x, aabb.minX, aabb.maxX),
                Mth.clamp(pos.y, aabb.minY, aabb.maxY),
                Mth.clamp(pos.z, aabb.minZ, aabb.maxZ)
        );
    }
}
