package net.quepierts.thatskyinteractions.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.quepierts.thatskyinteractions.block.entity.MuralBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MuralBlock extends BaseEntityBlock {
    public static final MapCodec<MuralBlock> CODEC = simpleCodec(MuralBlock::new);
    public static final BooleanProperty LIT;
    public MuralBlock(Properties properties) {
        super(properties);
    }

    @NotNull
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new MuralBlockEntity(blockPos, blockState);
    }

    @Override
    protected float getShadeBrightness(@NotNull BlockState blockState, @NotNull BlockGetter getter, @NotNull BlockPos pos) {
        return 1.0F;
    }

    static {
        LIT = BlockStateProperties.LIT;
    }
}
