package net.quepierts.thatskyinteractions.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.quepierts.thatskyinteractions.block.entity.CloudBlockEntity;
import org.jetbrains.annotations.Nullable;

public class CloudBlock extends BaseEntityBlock {
    public static final MapCodec<CloudBlock> CODEC = simpleCodec(CloudBlock::new);
    public CloudBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CloudBlockEntity(blockPos, blockState);
    }
}
