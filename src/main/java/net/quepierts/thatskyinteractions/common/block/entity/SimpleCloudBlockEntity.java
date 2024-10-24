package net.quepierts.thatskyinteractions.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.quepierts.thatskyinteractions.common.registry.BlockEntities;

public class SimpleCloudBlockEntity extends AbstractCloudBlockEntity {
    public SimpleCloudBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntities.SIMPLE_CLOUD.get(), pos, blockState);
    }
}
