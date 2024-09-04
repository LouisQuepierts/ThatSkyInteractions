package net.quepierts.thatskyinteractions.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.quepierts.thatskyinteractions.registry.BlockEntities;

public class SimpleCloudBlockEntity extends CloudBlockEntity {
    public SimpleCloudBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntities.SIMPLE_CLOUD.get(), pos, blockState);
    }
}
