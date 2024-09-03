package net.quepierts.thatskyinteractions.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.quepierts.thatskyinteractions.block.entity.WingOfLightBlockEntity;
import net.quepierts.thatskyinteractions.client.registry.Particles;
import net.quepierts.thatskyinteractions.registry.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WingOfLightBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<WingOfLightBlock> CODEC = simpleCodec(WingOfLightBlock::new);
    public static final EnumProperty<DoubleBlockHalf> HALF;
    public static final BooleanProperty WATERLOGGED;

    public WingOfLightBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF, WATERLOGGED);
    }

    /*@Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            BlockPos above = pos.above();
            level.setBlock(
                    above,
                    state.setValue(HALF, DoubleBlockHalf.UPPER).setValue(WATERLOGGED, level.getFluidState(above).getType() == Fluids.WATER),
                    3);
        }
    }*/

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            BlockPos above = pos.above();
            boolean isWater = level.getBlockState(above).getFluidState().getType() == Fluids.WATER;
            level.setBlock(
                    above,
                    this.defaultBlockState()
                            .setValue(HALF, DoubleBlockHalf.UPPER)
                            .setValue(WATERLOGGED, isWater),
                    3
            );
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return context.isHoldingItem(Items.WING_OF_LIGHT.get()) ? Shapes.block() : Shapes.empty();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();

        boolean isWater = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        if (blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(context)) {
            return this.defaultBlockState()
                    .setValue(HALF, DoubleBlockHalf.LOWER)
                    .setValue(WATERLOGGED, isWater);
        } else {
            return this.defaultBlockState()
                    .setValue(HALF, DoubleBlockHalf.UPPER)
                    .setValue(WATERLOGGED, isWater);
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0f;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @NotNull
    @Override
    protected RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        if (blockState.getValue(HALF) == DoubleBlockHalf.UPPER)
            return null;
        return new WingOfLightBlockEntity(blockPos, blockState);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER)
            return;
        float x = pos.getX() + 0.1f;
        float y = pos.getY();
        float z = pos.getZ() + 0.1f;

        for (int i = 0; i < 2; i++) {
            level.addParticle(
                    Particles.STAR.get(),
                    x + random.nextFloat() * 0.8f,
                    y + random.nextFloat() * 2.0f,
                    z + random.nextFloat() * 0.8f,
                    0, 0, 0
            );
        }
    }

    @Override
    protected void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean movedByPiston) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (newState.getBlock() instanceof WingOfLightBlock && newState.getValue(HALF) == half) {
            state.setValue(WATERLOGGED, newState.getValue(WATERLOGGED));
        } else {
            if (half == DoubleBlockHalf.LOWER) {
                BlockPos relative = pos.relative(Direction.UP);
                level.removeBlock(relative, false);
                super.onRemove(state, level, pos, newState, movedByPiston);
            } else {
                BlockPos relative = pos.relative(Direction.DOWN);
                level.removeBlock(relative, false);
            }
        }
    }

    static {
        HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
    }
}
