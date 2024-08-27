package net.quepierts.thatskyinteractions.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.quepierts.thatskyinteractions.client.gui.animate.AnimateUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.LerpNumberAnimation;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.component.w2s.World2ScreenWidget;
import net.quepierts.thatskyinteractions.client.gui.holder.FloatHolder;
import net.quepierts.thatskyinteractions.client.gui.layer.World2ScreenGridLayer;
import org.joml.Vector3f;

import java.util.UUID;

public class FakePlayerDisplayHandler {
    private final FloatHolder enterHolder = new FloatHolder(0.0f);
    private final LerpNumberAnimation enterAnimation = new LerpNumberAnimation(this.enterHolder, AnimateUtils.Lerp::smooth, 0, 1, 1.0f);
    private FakeClientPlayer player;
    private FakePlayerLightW2SWidget widget;
    private boolean pushed = false;

    public void init(ClientLevel level) {
        this.player = new FakeClientPlayer(level, this);
        this.widget = new FakePlayerLightW2SWidget(this.player, this.enterHolder);
    }

    public void reset() {
        if (this.player != null) {
            World2ScreenGridLayer.INSTANCE.remove(this.player.getUUID());
        }
        this.player = null;
        this.widget = null;
    }

    public void show(Vec3 pos, float yRot) {
        this.player.setPos(pos);
        this.player.setYRot(yRot);
        this.player.setYBodyRot(yRot);
        this.player.setYHeadRot(yRot);
        this.enterAnimation.reset(0, 1);
        ScreenAnimator.GLOBAL.play(this.enterAnimation);
        World2ScreenGridLayer.INSTANCE.addWorldPositionObject(this.player.getUUID(), widget);
    }

    public void hide() {
        this.enterAnimation.reset(1, 0);
        ScreenAnimator.GLOBAL.play(this.enterAnimation);
    }

    public boolean isVisible() {
        return this.enterHolder.getValue() != 0.0f;
    }

    public void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        if (!event.getEntity().equals(this.player))
            return;

        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();

        float value = this.enterHolder.getValue();
        poseStack.translate(0, 5 - 5 * value, 0);
        poseStack.scale(value, value, value);

        pushed = true;
    }

    public void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        if (!pushed)
            return;

        RenderSystem.setShaderColor(1, 1, 1, 1);
        event.getPoseStack().popPose();
        pushed = false;
    }

    public void setPlayerSkin(UUID uuid) {
        this.player.setPlayerSkin(uuid);
    }

    private static final class FakePlayerLightW2SWidget extends World2ScreenWidget {
        private final LivingEntity bound;
        private final FloatHolder enterHolder;
        public FakePlayerLightW2SWidget(LivingEntity bound, FloatHolder enterHolder) {
            this.bound = bound;
            this.enterHolder = enterHolder;
        }

        @Override
        public void render(GuiGraphics guiGraphics, boolean highlight, float value) {
            float enter = this.enterHolder.getValue();

            PoseStack pose = guiGraphics.pose();
            pose.pushPose();


            RenderUtils.drawCrossLightSpot(
                    guiGraphics,
                    this.x - 16, this.y - 16,
                    32,
                    (0.95f + Mth.sin((ScreenAnimator.GLOBAL.time()) * 12) * 0.02f) * (1.2f - enter),
                    2.0f, 0xffa4e5f7
            );

            pose.popPose();
        }

        @Override
        public boolean shouldRemove() {
            return this.enterHolder.getValue() > 0.95f;
        }

        @Override
        public void getWorldPos(Vector3f out) {
            Vec3 position = this.bound.position();
            out.set(
                    position.x(),
                    position.y() + 6f - enterHolder.getValue() * 5f,
                    position.z()
            );
        }
    }
}
