package net.quepierts.thatskyinteractions.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.quepierts.thatskyinteractions.registry.BlockEntities;
import net.quepierts.thatskyinteractions.registry.Items;
import org.jetbrains.annotations.NotNull;
import org.joml.*;

public class MuralBlockEntity extends AbstractUpdatableBlockEntity implements IUpdateMark {
    private static final String TAG_MURAL = "mural";
    private static final String TAG_SIZE = "size";
    private static final String TAG_OFFSET = "offset";
    private static final String TAG_ROTATE = "rotate";

    private final Vector3i offset = new Vector3i(0);
    private final Vector3i rotate = new Vector3i(0);
    private final Vector2i size = new Vector2i(16);

    @NotNull
    private AABB aabb;
    @NotNull
    private ResourceLocation muralTexture;
    private boolean recompile = true;

    public MuralBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntities.MURAL.get(), pos, blockState);
        this.muralTexture = ResourceLocation.withDefaultNamespace("textures/item/carrot.png");
        this.aabb = new AABB(pos);
    }

    @Override
    public void toNBT(@NotNull CompoundTag tag) {
        tag.putIntArray(TAG_SIZE, new int[]{this.size.x, this.size.y});
        tag.putIntArray(TAG_OFFSET, new int[]{this.offset.x, this.offset.y, this.offset.z});
        tag.putIntArray(TAG_ROTATE, new int[]{this.rotate.x, this.rotate.y, this.rotate.z});
        tag.putString(TAG_MURAL, muralTexture.toString());
    }

    @Override
    public void fromNBT(@NotNull CompoundTag tag) {
        if (tag.contains(TAG_SIZE)) {
            int[] array = tag.getIntArray(TAG_SIZE);
            if (array.length == 2) {
                this.setSize(array[0], array[1]);
            }
        }

        if (tag.contains(TAG_OFFSET)) {
            int[] array = tag.getIntArray(TAG_OFFSET);
            if (array.length == 3) {
                this.setOffset(array[0], array[1], array[2]);
            }
        }

        if (tag.contains(TAG_ROTATE)) {
            int[] array = tag.getIntArray(TAG_ROTATE);
            if (array.length == 3) {
                this.setRotate(array[0], array[1], array[2]);
            }
        }

        if (tag.contains(TAG_MURAL)) {
            this.muralTexture = ResourceLocation.parse(tag.getString(TAG_MURAL));
        }

        this.updateAABB();
    }

    public Vector2i getSize() {
        return new Vector2i(this.size);
    }

    public Vector2f getSizeF() {
        return new Vector2f(this.size);
    }

    public Vector3i getRotate() {
        return new Vector3i(this.rotate);
    }

    public Vector3f getRotateF() {
        return new Vector3f(this.rotate);
    }

    public Vector3i getOffset() {
        return new Vector3i(this.offset);
    }

    public Vector3f getOffsetF() {
        return new Vector3f(this.offset);
    }

    public void setOffset(int x, int y, int z) {
        this.offset.set(x, y, z);
        this.recompile = true;
    }

    public void setRotate(int x, int y, int z) {
        this.rotate.set(x, y, z);
        this.recompile = true;
    }

    public void setSize(int x, int y) {
        this.size.set(x, y);
        this.recompile = true;
    }

    public void setMuralTexture(@NotNull ResourceLocation muralTexture) {
        this.muralTexture = muralTexture;
        this.recompile = true;
    }

    public ResourceLocation getTextureLocation() {
        return this.muralTexture;
    }

    public void updateAABB() {
        BlockPos pos = this.getBlockPos();
        Matrix4f mat = new Matrix4f()
                .translate(
                        pos.getX() + 0.5f + this.offset.x / 16f,
                        pos.getY() + 0.5f + this.offset.y / 16f,
                        pos.getZ() + 0.5f + this.offset.z / 16f
                )
                .rotateYXZ(
                        this.rotate.y * Mth.DEG_TO_RAD,
                        this.rotate.x * Mth.DEG_TO_RAD,
                        this.rotate.z * Mth.DEG_TO_RAD
                );


        final float x = size.x() / 32f;
        final float y = size.y() / 32f;

        Vector3f a = mat.transformPosition(-x, -y, -0.1f, new Vector3f());
        Vector3f b = mat.transformPosition(x, y, 0.1f, new Vector3f());
        this.aabb = new AABB(
                a.x, a.y, a.z,
                b.x, b.y, b.z
        );
    }

    @NotNull
    public AABB getAabb() {
        return aabb;
    }

    @Override
    public boolean isEditorItem(ItemStack item) {
        return item.is(Items.MURAL);
    }

    public boolean isDirty() {
        return this.recompile;
    }

    public void setDirty(boolean recompile) {
        this.recompile = recompile;
    }
}
