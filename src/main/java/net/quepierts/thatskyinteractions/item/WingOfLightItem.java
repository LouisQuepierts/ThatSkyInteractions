package net.quepierts.thatskyinteractions.item;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.quepierts.thatskyinteractions.block.WingOfLightBlock;
import net.quepierts.thatskyinteractions.block.entity.WingOfLightBlockEntity;
import net.quepierts.thatskyinteractions.registry.Blocks;
import org.jetbrains.annotations.NotNull;

public class WingOfLightItem extends BlockItem {
    public WingOfLightItem() {
        super(Blocks.WING_OF_LIGHT.get(), new Properties());
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide) {
            return InteractionResult.sidedSuccess(true);
        }

        Player player = context.getPlayer();
        if (player != null && player.isCreative()) {
            BlockPos pos = context.getClickedPos();
            Level level = context.getLevel();
            BlockState state = level.getBlockState(pos);

            if (state.is(Blocks.WING_OF_LIGHT)) {
                DoubleBlockHalf half = state.getValue(WingOfLightBlock.HALF);
                BlockEntity entity = level.getBlockEntity(half == DoubleBlockHalf.LOWER ? pos : pos.offset(0, -1, 0));
                if (entity instanceof WingOfLightBlockEntity wol) {
                    wol.setRotation(
                            player.getXRot() * Mth.DEG_TO_RAD,
                            player.getYRot() * Mth.DEG_TO_RAD + Mth.PI
                    );
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.FAIL;
    }
}
