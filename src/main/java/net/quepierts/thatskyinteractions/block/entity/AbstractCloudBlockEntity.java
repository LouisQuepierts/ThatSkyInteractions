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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.block.ICloud;
import net.quepierts.thatskyinteractions.registry.Items;
import net.quepierts.thatskyinteractions.registry.TagKeys;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractCloudBlockEntity extends AbstractUpdatableBlockEntity implements ICloud {
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
    private final Vector3i offset = new Vector3i(0);
    private final Vector3i size = new Vector3i(16);
    private boolean collisible;
    private AABB aabb;
    private boolean recompile = true;

    protected AbstractCloudBlockEntity(BlockEntityType<? extends AbstractCloudBlockEntity> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.aabb = new AABB(pos);
        this.collisible = true;
    }

    @Override
    public void toNBT(@NotNull CompoundTag tag) {
        tag.putIntArray(TAG_SIZE, new int[]{this.size.x, this.size.y, this.size.z});
        tag.putIntArray(TAG_OFFSET, new int[]{this.offset.x, this.offset.y, this.offset.z});
        tag.putBoolean(TAG_COLLISIBLE, this.collisible);
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

        if (tag.contains(TAG_COLLISIBLE)) {
            this.collisible = tag.getBoolean(TAG_COLLISIBLE);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void setRemoved() {
        ThatSkyInteractions.getInstance().getClient().getCloudRenderer().removeCloud(this);
        super.setRemoved();
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

    public boolean isCollisible() {
        return collisible;
    }

    public void setCollisible(boolean collisible) {
        this.collisible = collisible;
    }

    public int getColor() {
        return 0xffffffff;
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
            /*double absY = position.y - cloud.aabb.maxY + 0.35;

            entity.setDeltaMovement(movement.add(
                    0,
                    absY < -0.5 ? 0.2 : Math.max(absY * absY * 0.943, 0.2),
                    0
            ));
            entity.resetFallDistance();*/

            double distance = cloud.aabb.maxY - position.y;

            if (!entity.onGround() && distance < 1) {
                double oscillationForce = Mth.sin((float) (distance * Mth.PI)) * 0.1;

                entity.setDeltaMovement(
                        movement.x,
                        movement.y + oscillationForce,
                        movement.z);
            }

            if (distance > 2.2) {
                entity.setDeltaMovement(
                        movement.x,
                        movement.y + Math.min(distance - 1.8, 0.5),
                        movement.z
                );
            }
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
