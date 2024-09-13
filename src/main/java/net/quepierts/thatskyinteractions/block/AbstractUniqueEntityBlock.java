package net.quepierts.thatskyinteractions.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.quepierts.thatskyinteractions.block.entity.AbstractUniqueBlockEntity;
import net.quepierts.thatskyinteractions.data.UniqueBlockEntitySavedData;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractUniqueEntityBlock extends BaseEntityBlock {
    protected AbstractUniqueEntityBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean movedByPiston) {
        if (level instanceof ServerLevel serverLevel && level.getBlockEntity(pos) instanceof AbstractUniqueBlockEntity entity) {
            UniqueBlockEntitySavedData data = UniqueBlockEntitySavedData.getData(serverLevel);
            data.add(entity);
        }
    }

    @Override
    protected void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean movedByPiston) {
        if (level instanceof ServerLevel serverLevel && level.getBlockEntity(pos) instanceof AbstractUniqueBlockEntity entity) {
            UniqueBlockEntitySavedData data = UniqueBlockEntitySavedData.getData(serverLevel);
            data.remove(entity);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
