package net.quepierts.thatskyinteractions.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class UUIDBlockEntity extends BlockEntity {
    private UUID uuid;

    public UUIDBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.uuid = UUID.randomUUID();
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("uuid")) {
            this.uuid = tag.getUUID("uuid");
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putUUID("uuid", this.uuid);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag(@NotNull HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putUUID("uuid", this.uuid);
        return tag;
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider lookupProvider) {
        if (tag.contains("uuid")) {
            this.uuid = tag.getUUID("uuid");
        }
        super.handleUpdateTag(tag, lookupProvider);
    }

    public UUID getUUID() {
        return this.uuid;
    }
}
