package net.quepierts.thatskyinteractions.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.HitResult;
import net.quepierts.thatskyinteractions.common.block.entity.MuralBlockEntity;
import net.quepierts.thatskyinteractions.common.registry.Items;
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

    @NotNull
    @Override
    public ItemStack getCloneItemStack(
            @NotNull BlockState state,
            @NotNull HitResult target,
            @NotNull LevelReader level,
            @NotNull BlockPos pos,
            @NotNull Player player
    ) {
        if (level.getBlockEntity(pos) instanceof MuralBlockEntity mural) {
            if (mural.shouldBloom()) {
                return new ItemStack(Items.MURAL_BLOOMING);
            }
        }
        return new ItemStack(Items.MURAL);
    }

    @Override
    protected float getShadeBrightness(
            @NotNull BlockState blockState,
            @NotNull BlockGetter getter,
            @NotNull BlockPos pos
    ) {
        return 1.0F;
    }

    static {
        LIT = BlockStateProperties.LIT;
    }
}
