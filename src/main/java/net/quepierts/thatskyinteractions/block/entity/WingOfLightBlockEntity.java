package net.quepierts.thatskyinteractions.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;
import net.quepierts.thatskyinteractions.client.gui.component.w2s.HaloEffectW2SWidget;
import net.quepierts.thatskyinteractions.client.gui.component.w2s.PickupWingOfLightW2SButton;
import net.quepierts.thatskyinteractions.client.gui.component.w2s.World2ScreenWidget;
import net.quepierts.thatskyinteractions.registry.BlockEntities;
import org.jetbrains.annotations.NotNull;

public class WingOfLightBlockEntity extends W2SWidgetProviderBlockEntity {
    private static final String TAG_XROT = "xRot";
    private static final String TAG_YROT = "yRot";

    @OnlyIn(Dist.CLIENT)
    private HaloEffectW2SWidget halo;
    @OnlyIn(Dist.CLIENT)
    private PickupWingOfLightW2SButton pickupButton;
    private float xRot;
    private float yRot;
    public WingOfLightBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntities.WING_OF_LIGHT_BE.get(), pos, blockState);

        if (FMLEnvironment.dist.isClient()) {
            halo = new HaloEffectW2SWidget(this);
            pickupButton = new PickupWingOfLightW2SButton(this);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(TAG_XROT)) {
            this.xRot = tag.getFloat(TAG_XROT);
        }

        if (tag.contains(TAG_YROT)) {
            this.yRot = tag.getFloat(TAG_YROT);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putFloat(TAG_XROT, this.xRot);
        tag.putFloat(TAG_YROT, this.yRot);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putFloat(TAG_XROT, this.xRot);
        tag.putFloat(TAG_YROT, this.yRot);
        return tag;
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider) {
        super.handleUpdateTag(tag, lookupProvider);
        if (tag.contains(TAG_XROT)) {
            this.xRot = tag.getFloat(TAG_XROT);
        }

        if (tag.contains(TAG_YROT)) {
            this.yRot = tag.getFloat(TAG_YROT);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public World2ScreenWidget provideW2SWidget(float distanceSqr) {
        return distanceSqr > 8 ? this.halo : this.pickupButton;
    }

    public float getXRot() {
        return this.xRot;
    }

    public float getYRot() {
        return yRot;
    }

    public void setXRot(float xRot) {
        this.xRot = xRot;
    }

    public void setYRot(float yRot) {
        this.yRot = yRot;
    }
}
