package net.quepierts.thatskyinteractions.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;
import net.quepierts.thatskyinteractions.block.entity.CandleClusterBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.function.ToIntFunction;

public class CandleClusterBlock extends BaseEntityBlock {
    public static final BooleanProperty LIT;
    public static final ToIntFunction<BlockState> LIGHT_EMISSION;

    public static final MapCodec<CandleClusterBlock> CODEC = simpleCodec(CandleClusterBlock::new);
    public CandleClusterBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
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
        return Shapes.empty();
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
                    if (entity.tryRemoveCandle(localX, localZ)) {
                        this.spawnDestroyParticles(level, player, pos, state);
                        return ItemInteractionResult.sidedSuccess(level.isClientSide);
                    }
                } if (entity.tryExtinguishCandle(localX, localZ)) {
                    level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
            } else if (stack.is(Tags.Items.TOOLS_IGNITER)) {
                if (entity.tryLitCandle(localX, localZ)) {
                    level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }
        return ItemInteractionResult.FAIL;
    }

    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (level.getBlockEntity(pos) instanceof CandleClusterBlockEntity entity) {
            double y = pos.getY() + 0.625;
            for (Vector2i position : entity.getLitPositions()) {
                this.addParticlesAndSound(
                        level,
                        pos.getX() + 0.0625 + position.x() / 16.0,
                        y,
                        pos.getZ() + 0.0625 + position.y() / 16.0,
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
        LIT = BlockStateProperties.LIT;
        LIGHT_EMISSION = (state) -> state.getValue(LIT) ? 15 : 0;
    }
}
