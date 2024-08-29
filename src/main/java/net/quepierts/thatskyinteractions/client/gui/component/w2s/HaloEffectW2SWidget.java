package net.quepierts.thatskyinteractions.client.gui.component.w2s;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.client.util.RayTraceUtil;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class HaloEffectW2SWidget extends World2ScreenWidget {
    @NotNull
    private final BlockEntity bound;
    public HaloEffectW2SWidget(@NotNull BlockEntity bound) {
        this.bound = bound;
        BlockPos position = this.bound.getBlockPos();
        this.worldPos.set(position.getX() + 0.5f, position.getY() + 1.0f, position.getZ() + 0.5f);
    }

    @Override
    public void render(GuiGraphics guiGraphics, boolean highlight, float value, float deltaTicks) {
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();

        pose.translate(this.x, this.y, 0);
        pose.scale(this.scale, this.scale * 0.9f, 1);
        RenderUtils.drawHalo(
                guiGraphics,
                - 100,  - 100,
                200, 1.0f, 0xfffffee0
        );

        pose.popPose();
    }

    @Override
    public boolean shouldRemove() {
        return this.bound.isRemoved();
    }

    @Override
    public void getWorldPos(Vector3f out) {
        out.set(this.worldPos);
    }

    @Override
    public boolean shouldRender() {
        if (!super.shouldRender()) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        Entity camera = minecraft.getCameraEntity();

        return !RayTraceUtil.isBlockedBySolidBlock(
                level,
                camera.getEyePosition().toVector3f().sub(0.5f, 0.5f, 0.5f),
                new Vector3f(this.worldPos).sub(0.5f, 0.5f, 0.5f),
                0.5f
        );
    }

    @Override
    public void calculateRenderScale(float distanceSqr) {
        if (distanceSqr < 8 || distanceSqr > 4096) {
            this.scale = 0;
        } else if (distanceSqr < 64) {
            this.scale = (distanceSqr - 8) / 56f;
        } else {
            float v = distanceSqr - 64;
            this.scale = 1 - (v * v) / (4032 * 4032);
        }
    }
}
