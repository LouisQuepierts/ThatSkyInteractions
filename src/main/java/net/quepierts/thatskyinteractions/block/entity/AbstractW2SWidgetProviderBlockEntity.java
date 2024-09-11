package net.quepierts.thatskyinteractions.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.client.gui.component.w2s.World2ScreenWidget;

public abstract class AbstractW2SWidgetProviderBlockEntity extends AbstractUniqueBlockEntity {
    public AbstractW2SWidgetProviderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @OnlyIn(Dist.CLIENT)
    public abstract World2ScreenWidget provideW2SWidget(float distanceSqr);
}
