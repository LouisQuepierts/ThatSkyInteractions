package net.quepierts.thatskyinteractions.client.gui.component.w2s;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.component.w2s.World2ScreenWidget;
import net.quepierts.thatskyinteractions.client.gui.holder.FloatHolder;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;
import org.joml.Vector3f;

import java.util.UUID;

public final class FakePlayerLightW2SWidget extends World2ScreenWidget {
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
