package net.quepierts.thatskyinteractions.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.Vec3;
import net.quepierts.thatskyinteractions.block.entity.CandleClusterBlockEntity;
import net.quepierts.thatskyinteractions.registry.Blocks;
import org.jetbrains.annotations.NotNull;

public class CandleClusterItem extends BlockItem {
    public CandleClusterItem() {
        super(Blocks.CANDLE_CLUSTER.get(), new Properties());
    }

    @NotNull
    @Override
    public InteractionResult useOn(@NotNull UseOnContext context) {
        if (context.getClickedFace() != Direction.UP) {
            return InteractionResult.PASS;
        }

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        BlockPos above = pos.above();
        if (level.getBlockEntity(above) instanceof CandleClusterBlockEntity candleCluster) {
            return this.placeInner(candleCluster, context);
        } else {
            InteractionResult result = super.useOn(context);

            if (level.getBlockEntity(above) instanceof CandleClusterBlockEntity candleCluster) {
                return this.placeInner(candleCluster, context);
            }

            return result;
        }
    }



    private InteractionResult placeInner(@NotNull CandleClusterBlockEntity entity, @NotNull UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Vec3 location = context.getClickLocation();
        Level level = context.getLevel();
        Player player = context.getPlayer();

        if (player == null) {
            return InteractionResult.PASS;
        }

        int localX = (int) ((location.x - pos.getX()) * 16);
        int localZ = (int) ((location.z - pos.getZ()) * 16);


        boolean success = entity.tryAddCandle(localX, localZ);
        if (success) {
            SoundType soundtype = SoundType.CANDLE;
            level.playSound(player, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
            context.getItemInHand().consume(1, player);
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }
}
