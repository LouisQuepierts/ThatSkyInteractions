package net.quepierts.thatskyinteractions.client.gui.component.w2s;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;
import org.joml.Vector3f;

public class LightEffectW2SWidget extends World2ScreenWidget {
    private final LivingEntity bound;
    public LightEffectW2SWidget(LivingEntity bound) {
        this.bound = bound;
    }

    @Override
    public void render(GuiGraphics guiGraphics, boolean highlight, float value) {
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();

        RenderUtils.drawCrossLightSpot(
                guiGraphics,
                this.x - 16, this.y - 16,
                32,
                0.95f + Mth.sin((ScreenAnimator.GLOBAL.time()) * 12) * 0.02f,
                2.0f, 0xffa4e5f7
        );

        pose.popPose();
    }

    @Override
    public void getWorldPos(Vector3f out) {
        Vec3 position = this.bound.getEyePosition();
        out.set(position.x(), position.y(), position.z());
    }
}
