package net.quepierts.thatskyinteractions.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;
import net.quepierts.thatskyinteractions.client.gui.component.w2s.World2ScreenButton;
import net.quepierts.thatskyinteractions.client.gui.component.w2s.World2ScreenWidget;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractW2SWidgetProviderBlockEntity extends AbstractUniqueBlockEntity {
    @Nullable
    @OnlyIn(Dist.CLIENT)
    protected World2ScreenButton button;
    public AbstractW2SWidgetProviderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);

        if (FMLEnvironment.dist.isClient()) {
            this.button = this.createButton();
        }
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    protected World2ScreenButton createButton() {
        return null;
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public World2ScreenWidget provideW2SWidget(float distanceSqr) {
        return this.button;
    }
}
