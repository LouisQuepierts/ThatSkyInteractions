package net.quepierts.thatskyinteractions.client.gui.component.w2s;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.simpleanimator.core.SimpleAnimator;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.gui.animate.AnimateUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.animate.WaitAnimation;
import net.quepierts.thatskyinteractions.client.gui.layer.World2ScreenWidgetLayer;
import net.quepierts.thatskyinteractions.common.block.entity.CandleClusterBlockEntity;
import net.quepierts.thatskyinteractions.common.data.attachment.UserDataAttachment;
import net.quepierts.thatskyinteractions.common.data.attachment.component.PickupComponent;
import net.quepierts.thatskyinteractions.common.network.packet.blockentity.PickablePickupPacket;
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
        ScreenAnimator.GLOBAL.play(new WaitAnimation(0.5f, () -> {
            LocalPlayer player = Minecraft.getInstance().player;

            if (player == null) {
                return;
            }

            UserDataAttachment attachment = UserDataAttachment.getAttachment(player);
            PickupComponent pickupComponent = attachment.getPickup();

            CandleClusterBlockEntity pickable = this.bound;
            if (pickupComponent.tryPickUp(pickable)) {
                this.setRemoved();
                World2ScreenWidgetLayer.INSTANCE.remove(pickable.getUUID());
                SimpleAnimator.getNetwork().update(new PickablePickupPacket(pickable));
            }
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
        BlockPos position = this.bound.getBlockPos();
        this.worldPos.y = position.getY() + 1.2f;
        if (this.bound.isOnSlab()) {
            this.worldPos.y -= 0.5f;
        }
        out.set(this.worldPos);
    }

    @Override
    public void calculateRenderScale(float distance) {
        this.scale = (float) AnimateUtils.Lerp.smooth(0, 1, 1.0f - Math.max(distance - 4, 0) / 4);
    }
}
