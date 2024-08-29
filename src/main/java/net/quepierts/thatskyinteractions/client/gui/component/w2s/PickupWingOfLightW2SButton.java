package net.quepierts.thatskyinteractions.client.gui.component.w2s;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.block.entity.WingOfLightBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class PickupWingOfLightW2SButton extends World2ScreenButton {
    public static final ResourceLocation TEXTURE = ThatSkyInteractions.getLocation("textures/gui/pickup.png");
    @NotNull
    private final WingOfLightBlockEntity bound;
    public PickupWingOfLightW2SButton(@NotNull WingOfLightBlockEntity bound) {
        super(TEXTURE);
        this.bound = bound;
        BlockPos position = this.bound.getBlockPos();
        this.worldPos.set(position.getX() + 0.5f, position.getY() + 1.0f, position.getZ() + 0.5f);
    }

    @Override
    public void invoke() {
        super.invoke();
    }

    @Override
    public boolean shouldRemove() {
        return this.bound.isRemoved();
    }

    @Override
    public void getWorldPos(Vector3f out) {
        out.set(this.worldPos);
    }
}
