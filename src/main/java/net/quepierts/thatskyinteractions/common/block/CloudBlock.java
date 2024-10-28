package net.quepierts.thatskyinteractions.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.quepierts.thatskyinteractions.common.block.entity.AbstractCloudBlockEntity;
import net.quepierts.thatskyinteractions.common.block.entity.SimpleCloudBlockEntity;
import net.quepierts.thatskyinteractions.common.item.CloudItem;
import net.quepierts.thatskyinteractions.common.registry.BlockEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CloudBlock extends BaseEntityBlock {
    public static final MapCodec<CloudBlock> CODEC = simpleCodec(CloudBlock::new);
    public CloudBlock(Properties properties) {
        super(properties);
    }

    @NotNull
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, BlockEntities.SIMPLE_CLOUD.get(), AbstractCloudBlockEntity::tick);
    }

    @NotNull
    @Override
    protected ItemInteractionResult useItemOn(
            @NotNull ItemStack stack,
            @NotNull BlockState state,
            @NotNull Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull InteractionHand hand,
            @NotNull BlockHitResult hitResult
    ) {

        if (level.getBlockEntity(pos) instanceof AbstractCloudBlockEntity entity) {
            if (stack.getItem() instanceof CloudItem) {
                entity.setCollisible(!entity.isCollisible());
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new SimpleCloudBlockEntity(blockPos, blockState);
    }
}
