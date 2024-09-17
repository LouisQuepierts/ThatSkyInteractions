package net.quepierts.thatskyinteractions.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public abstract class AbstractUniqueBlockEntity extends AbstractUpdatableBlockEntity {
    private UUID uuid;

    public AbstractUniqueBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);

        String name = this.type() + ":" + pos.getX() + "," + pos.getY() + "," + pos.getZ();
        this.uuid = UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
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

    public abstract ResourceLocation type();

    public boolean shouldRecord() {
        return true;
    }
}
