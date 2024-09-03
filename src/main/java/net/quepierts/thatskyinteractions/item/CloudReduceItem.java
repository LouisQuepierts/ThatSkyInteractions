package net.quepierts.thatskyinteractions.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.quepierts.thatskyinteractions.block.entity.CloudBlockEntity;
import net.quepierts.thatskyinteractions.registry.Blocks;
import org.jetbrains.annotations.NotNull;

public class CloudReduceItem extends Item {
    public CloudReduceItem() {
        super(new Properties());
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null && player.isCreative()) {
            BlockPos pos = context.getClickedPos();
            Level level = context.getLevel();
            BlockState state = level.getBlockState(pos);

            if (state.is(Blocks.CLOUD)) {
                BlockEntity entity = level.getBlockEntity(pos);
                if (entity instanceof CloudBlockEntity cloud) {
                    cloud.reduce(context.getClickedFace(), player.isShiftKeyDown() ? 1 : 16);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.useOn(context);
    }
}
