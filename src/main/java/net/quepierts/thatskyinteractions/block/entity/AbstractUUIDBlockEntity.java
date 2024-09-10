package net.quepierts.thatskyinteractions.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class AbstractUUIDBlockEntity extends AbstractUpdatableBlockEntity {
    private UUID uuid;

    public AbstractUUIDBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.uuid = UUID.randomUUID();
    }

    @Override
    public void toNBT(@NotNull CompoundTag tag) {
        tag.putUUID("uuid", this.uuid);
    }

    @Override
    public void fromNBT(@NotNull CompoundTag tag) {
        if (tag.contains("uuid")) {
            this.uuid = tag.getUUID("uuid");
        }
    }

    public UUID getUUID() {
        return this.uuid;
    }
}
