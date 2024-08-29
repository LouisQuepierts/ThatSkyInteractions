package net.quepierts.thatskyinteractions.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.client.gui.component.w2s.World2ScreenWidget;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class W2SWidgetProviderBlockEntity extends BlockEntity {
    private UUID uuid;
    public W2SWidgetProviderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.uuid = UUID.randomUUID();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("uuid")) {
            this.uuid = tag.getUUID("uuid");
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putUUID("uuid", this.uuid);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putUUID("uuid", this.uuid);
        return tag;
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider) {
        if (tag.contains("uuid")) {
            this.uuid = tag.getUUID("uuid");
        }
        super.handleUpdateTag(tag, lookupProvider);
    }

    @OnlyIn(Dist.CLIENT)
    public abstract World2ScreenWidget provideW2SWidget(float distanceSqr);

    public UUID getUUID() {
        return this.uuid;
    }
}
