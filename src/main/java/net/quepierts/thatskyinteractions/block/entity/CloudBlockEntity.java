package net.quepierts.thatskyinteractions.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.quepierts.thatskyinteractions.registry.BlockEntities;

public class CloudBlockEntity extends BlockEntity {
    public CloudBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntities.CLOUD_BE.get(), pos, blockState);
    }
}
