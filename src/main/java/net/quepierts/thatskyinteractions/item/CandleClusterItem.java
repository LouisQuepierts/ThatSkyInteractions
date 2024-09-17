package net.quepierts.thatskyinteractions.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.quepierts.thatskyinteractions.block.CandleType;
import net.quepierts.thatskyinteractions.block.entity.CandleClusterBlockEntity;
import net.quepierts.thatskyinteractions.registry.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CandleClusterItem extends BlockItem {
    private final CandleType type;
    private final Component scaleDescription;
    private final Component framedDescription;
    public CandleClusterItem(CandleType type) {
        super(Blocks.CANDLE_CLUSTER.get(), new Properties());
        this.type = type;
        this.scaleDescription = Component.translatable("candle_cluster.scale", type.getSize(), type.getSize(), type.getHeight()).withStyle(ChatFormatting.GRAY);
        this.framedDescription =  Component.translatable("candle_cluster.framed", type.isFramed()).withStyle(ChatFormatting.GRAY);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(this.scaleDescription);
        tooltipComponents.add(this.framedDescription);
    }

    @NotNull
    @Override
    public InteractionResult useOn(@NotNull UseOnContext context) {
        final Player player = context.getPlayer();
        final Level level = context.getLevel();

        final BlockPos pos = context.getClickedPos();
        final Vec3 location = context.getClickLocation();

        if (player == null || !player.getAbilities().mayBuild) {
            return InteractionResult.PASS;
        }

        int localX = (int) ((location.x - pos.getX()) * 16);
        int localZ = (int) ((location.z - pos.getZ()) * 16);

        if (player.isShiftKeyDown()) {
            BlockState state = level.getBlockState(pos);
            if (state.is(Blocks.CANDLE_CLUSTER)) {
                BlockEntity entity = level.getBlockEntity(pos);

                if (entity instanceof CandleClusterBlockEntity candleClusterBlockEntity) {
                    if (candleClusterBlockEntity.tryRemoveCandle(localX, localZ, player)) {
                        return InteractionResult.sidedSuccess(level.isClientSide);
                    }
                }
            }
        }

        BlockPos above = pos.above();
        if (level.getBlockEntity(above) instanceof CandleClusterBlockEntity candleCluster) {
            return this.placeInner(candleCluster, context, localX, localZ);
        } else if (!CandleClusterBlockEntity.isPlacePositionInvalid(localX, localZ, type.getSize())) {
            if (context.getClickedFace() != Direction.UP) {
                return InteractionResult.PASS;
            }

            if (type.isDoubleBlock() && !level.getBlockState(above.above()).isAir()) {
                return InteractionResult.PASS;
            }

            InteractionResult result = super.useOn(context);

            if (level.getBlockEntity(above) instanceof CandleClusterBlockEntity candleCluster) {
                return this.placeInner(candleCluster, context, localX, localZ);
            }

            return result;
        }

        return InteractionResult.PASS;
    }

    private InteractionResult placeInner(@NotNull CandleClusterBlockEntity entity, @NotNull UseOnContext context, final int localX, final int localZ) {
        final BlockPos pos = context.getClickedPos();
        final Level level = context.getLevel();
        final Player player = context.getPlayer();

        if (player == null) {
            return InteractionResult.PASS;
        }

        int rotation = player.isShiftKeyDown() ?
                CandleClusterBlockEntity.MAX_ROTATION - (int) (player.getYRot() / CandleClusterBlockEntity.UNIT_ROTATION_DEG) :
                level.getRandom().nextInt(0, CandleClusterBlockEntity.MAX_ROTATION);

        boolean success = entity.tryAddCandle(localX, localZ, type, rotation);
        if (success) {
            context.getItemInHand().consume(1, player);
            SoundType soundtype = SoundType.CANDLE;
            level.playSound(player, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }
}
