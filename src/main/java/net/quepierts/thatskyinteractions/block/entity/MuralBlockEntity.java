package net.quepierts.thatskyinteractions.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.registry.BlockEntities;
import org.jetbrains.annotations.NotNull;

public class MuralBlockEntity extends BlockEntity {
    private static final String TAG_MURAL = "mural";
    @NotNull
    private ResourceLocation muralTexture;
    public MuralBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntities.MURAL.get(), pos, blockState);
        this.muralTexture = ThatSkyInteractions.getLocation("empty");
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(TAG_MURAL)) {
            this.muralTexture = ResourceLocation.parse(tag.getString(TAG_MURAL));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString(TAG_MURAL, muralTexture.toString());
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putString(TAG_MURAL, muralTexture.toString());
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.handleUpdateTag(tag, lookupProvider);
        if (tag.contains(TAG_MURAL)) {
            this.muralTexture = ResourceLocation.parse(tag.getString(TAG_MURAL));
        }
    }
}
