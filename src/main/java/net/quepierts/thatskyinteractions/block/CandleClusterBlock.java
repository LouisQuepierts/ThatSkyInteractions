package net.quepierts.thatskyinteractions.block;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.quepierts.thatskyinteractions.block.entity.CandleClusterBlockEntity;
import net.quepierts.thatskyinteractions.item.CandleClusterItem;
import net.quepierts.thatskyinteractions.registry.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.ToIntFunction;

public class CandleClusterBlock extends BaseEntityBlock {
    public static final IntegerProperty LEVEL;
    public static final ToIntFunction<BlockState> LIGHT_EMISSION;
    private static final VoxelShape AABB;

    public static final MapCodec<CandleClusterBlock> CODEC = simpleCodec(CandleClusterBlock::new);
    public CandleClusterBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(LEVEL, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @NotNull
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @NotNull
    @Override
    protected VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if (level.getBlockEntity(pos) instanceof CandleClusterBlockEntity entity) {
            return entity.getShape();
        }
        return AABB;
    }

    @NotNull
    @Override
    public ItemStack getCloneItemStack(@NotNull BlockState state, @NotNull HitResult target, @NotNull LevelReader level, @NotNull BlockPos pos, @NotNull Player player) {
        if (level.getBlockEntity(pos) instanceof CandleClusterBlockEntity entity) {
            Vec3 location = target.getLocation();
            int localX = (int) ((location.x - pos.getX()) * 16);
            int localZ = (int) ((location.z - pos.getZ()) * 16);

            short candle = entity.getCandle(localX, localZ);

            if (candle != 0) {
                CandleType type = CandleClusterBlockEntity.getCandleType(candle);
                DeferredHolder<Item, CandleClusterItem> holder = Items.CANDLES[type.ordinal()];
                return new ItemStack(holder);
            }
        }
        return ItemStack.EMPTY;
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
        if (!player.getAbilities().mayBuild) {
            return ItemInteractionResult.FAIL;
        }

        if (level.getBlockEntity(pos) instanceof CandleClusterBlockEntity entity) {
            Vec3 location = hitResult.getLocation();

            int localX = (int) ((location.x - pos.getX()) * 16);
            int localZ = (int) ((location.z - pos.getZ()) * 16);

            if (stack.isEmpty()) {
                if (player.isShiftKeyDown()) {
                    if (entity.tryRemoveCandle(localX, localZ, player)) {
                        return ItemInteractionResult.sidedSuccess(level.isClientSide);
                    }
                } if (entity.tryExtinguishCandle(localX, localZ)) {
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
            } else if (stack.is(Tags.Items.TOOLS_IGNITER)) {
                if (entity.tryLitCandle(localX, localZ) || entity.tryLitAny()) {
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }
        return ItemInteractionResult.FAIL;
    }

    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (level.getBlockEntity(pos) instanceof CandleClusterBlockEntity entity) {
            ShortArrayList lighted = entity.getLightedCandles();
            for (int i = 0; i < lighted.size(); i++) {
                short candle = lighted.getShort(i);
                CandleType type = CandleClusterBlockEntity.getCandleType(candle);
                double half = type.getSize() / 32.0;
                this.addParticlesAndSound(
                        level,
                        pos.getX() + half + CandleClusterBlockEntity.getCandleX(candle) / 16.0,
                        pos.getY() + 0.125 + type.getHeight() / 16.0,
                        pos.getZ() + half + CandleClusterBlockEntity.getCandleZ(candle) / 16.0,
                        random
                );
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new CandleClusterBlockEntity(blockPos, blockState);
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

    private void addParticlesAndSound(Level level, double x, double y, double z, RandomSource random) {
        float f = random.nextFloat();
        if (f < 0.3F) {
            level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0);
            if (f < 0.17F) {
                level.playLocalSound(
                        x + 0.5,
                        y + 0.5,
                        z + 0.5,
                        SoundEvents.CANDLE_AMBIENT,
                        SoundSource.BLOCKS,
                        1.0F + random.nextFloat(),
                        random.nextFloat() * 0.7F + 0.3F,
                        false
                );
            }
        }

        level.addParticle(ParticleTypes.SMALL_FLAME, x, y, z, 0.0, 0.0, 0.0);
    }

    static {
        LEVEL = BlockStateProperties.LEVEL;
        LIGHT_EMISSION = (state) -> state.getValue(LEVEL);

        AABB = Block.box(0, 0, 0, 16, 32, 16);
    }
}
