package net.quepierts.thatskyinteractions.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.quepierts.thatskyinteractions.block.ICloud;
import net.quepierts.thatskyinteractions.registry.BlockEntities;
import org.jetbrains.annotations.NotNull;

public class ColoredCloudBlockEntity extends AbstractCloudBlockEntity implements ICloud {
    private static final String TAG_COLOR = "color";
    private int color = 0xFFFFFFFF;
    public ColoredCloudBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntities.COLORED_CLOUD.get(), pos, blockState);
    }

    @Override
    public void toNBT(@NotNull CompoundTag tag) {
        super.toNBT(tag);
        tag.putInt(TAG_COLOR, this.color);
    }

    @Override
    public void fromNBT(@NotNull CompoundTag tag) {
        super.fromNBT(tag);
        if (tag.contains(TAG_COLOR)) {
            this.setColor(tag.getInt(TAG_COLOR));
        }
    }

    public void setColor(int color) {
        if (this.color != color) {
            this.color = color;
        }
    }

    public int getColor() {
        return color;
    }
}
