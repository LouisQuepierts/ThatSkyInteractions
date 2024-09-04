package net.quepierts.thatskyinteractions.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.quepierts.thatskyinteractions.block.ICloud;
import net.quepierts.thatskyinteractions.registry.BlockEntities;
import org.jetbrains.annotations.NotNull;

public class ColoredCloudBlockEntity extends CloudBlockEntity implements ICloud {
    private static final String TAG_COLOR = "color";
    private int color = 0xFFFFFFFF;
    public ColoredCloudBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntities.COLORED_CLOUD.get(), pos, blockState);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (tag.contains(TAG_COLOR)) {
            this.setColor(tag.getInt(TAG_COLOR));
        }
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider lookupProvider) {
        super.handleUpdateTag(tag, lookupProvider);

        if (tag.contains(TAG_COLOR)) {
            this.setColor(tag.getInt(TAG_COLOR));
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(TAG_COLOR, this.color);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(@NotNull HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt(TAG_COLOR, this.color);
        return tag;
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
