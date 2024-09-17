package net.quepierts.thatskyinteractions.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.gui.component.w2s.HaloEffectW2SWidget;
import net.quepierts.thatskyinteractions.client.gui.component.w2s.PickupWingOfLightW2SButton;
import net.quepierts.thatskyinteractions.client.gui.component.w2s.World2ScreenButton;
import net.quepierts.thatskyinteractions.client.gui.component.w2s.World2ScreenWidget;
import net.quepierts.thatskyinteractions.registry.BlockEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WingOfLightBlockEntity extends AbstractW2SWidgetProviderBlockEntity implements IPickable {
    public static final ResourceLocation TYPE = ThatSkyInteractions.getLocation("wing_of_light");
    private static final String TAG_XROT = "xRot";
    private static final String TAG_YROT = "yRot";

    @OnlyIn(Dist.CLIENT)
    private HaloEffectW2SWidget halo;
    private float xRot;
    private float yRot;
    public WingOfLightBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntities.WING_OF_LIGHT.get(), pos, blockState);
    }

    @Override
    public void toNBT(@NotNull CompoundTag tag) {
        super.toNBT(tag);
        tag.putFloat(TAG_XROT, this.xRot);
        tag.putFloat(TAG_YROT, this.yRot);
    }

    @Override
    public void fromNBT(@NotNull CompoundTag tag) {
        super.fromNBT(tag);
        if (tag.contains(TAG_XROT)) {
            this.xRot = tag.getFloat(TAG_XROT);
        }

        if (tag.contains(TAG_YROT)) {
            this.yRot = tag.getFloat(TAG_YROT);
        }
    }

    @Override
    public ResourceLocation type() {
        return TYPE;
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    @Override
    protected World2ScreenButton createButton() {
        this.halo = new HaloEffectW2SWidget(this);
        return new PickupWingOfLightW2SButton(this);
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    @Override
    public World2ScreenWidget provideW2SWidget(float distanceSqr) {
        return distanceSqr > 8 ? this.halo : this.button;
    }


    @Override
    public void onPickup(ServerPlayer player) {
        player.addItem(new ItemStack(Items.RED_CANDLE));
    }

    public float getXRot() {
        return this.xRot;
    }

    public float getYRot() {
        return yRot;
    }

    public void setRotation(float xRot, float yRot) {
        this.xRot = xRot;
        this.yRot = yRot;
        this.markUpdate();
    }
}
