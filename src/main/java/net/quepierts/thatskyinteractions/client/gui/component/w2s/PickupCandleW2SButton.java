package net.quepierts.thatskyinteractions.client.gui.component.w2s;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.block.entity.CandleClusterBlockEntity;
import net.quepierts.thatskyinteractions.client.data.ClientTSIDataCache;
import net.quepierts.thatskyinteractions.client.gui.animate.AnimateUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.animate.WaitAnimation;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class PickupCandleW2SButton extends World2ScreenButton {
    public static final ResourceLocation TEXTURE = ThatSkyInteractions.getLocation("textures/gui/pickup.png");
    private final CandleClusterBlockEntity bound;
    public PickupCandleW2SButton(CandleClusterBlockEntity bound) {
        super(TEXTURE);
        this.bound = bound;
        BlockPos position = this.bound.getBlockPos();
        this.worldPos.set(position.getX() + 0.5f, position.getY() + 1.2f, position.getZ() + 0.5f);
        this.limitInScreen = false;
    }

    @Override
    public void invoke() {
        ClientTSIDataCache cache = ThatSkyInteractions.getInstance().getClient().getCache();
        ScreenAnimator.GLOBAL.play(new WaitAnimation(0.5f, () -> {
            cache.pickup(this.bound, true);
            PickupCandleW2SButton.this.setRemoved();
        }));
    }

    @Override
    public boolean shouldRender() {
        return !this.shouldSkip() && super.shouldRender();
    }

    @Override
    public boolean shouldRemove() {
        return this.bound.isRemoved() || !this.bound.canReward() || !this.bound.isLighted();
    }

    @Override
    public void getWorldPos(Vector3f out) {
        out.set(this.worldPos);
    }

    @Override
    public void calculateRenderScale(float distance) {
        this.scale = (float) AnimateUtils.Lerp.smooth(0, 1, 1.0f - Math.max(distance - 4, 0) / 4);
    }
}
