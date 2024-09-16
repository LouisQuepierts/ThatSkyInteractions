package net.quepierts.thatskyinteractions.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class HugeCandleClusterBlock extends Block {

    public HugeCandleClusterBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(CandleClusterBlock.LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CandleClusterBlock.LIT);
    }

    @NotNull
    protected VoxelShape getVisualShape(
            @NotNull BlockState state,
            @NotNull BlockGetter getter,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context
    ) {
        return Shapes.empty();
    }

    protected float getShadeBrightness(
            @NotNull BlockState p_308911_,
            @NotNull BlockGetter p_308952_,
            @NotNull BlockPos p_308918_
    ) {
        return 1.0F;
    }

    protected boolean propagatesSkylightDown(
            @NotNull BlockState p_309084_,
            @NotNull BlockGetter p_309133_,
            @NotNull BlockPos p_309097_
    ) {
        return true;
    }
}
