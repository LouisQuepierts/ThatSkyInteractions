package net.quepierts.thatskyinteractions.client.gui.component.w2s;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.client.registry.PostEffects;
import net.quepierts.thatskyinteractions.client.registry.RenderTypes;
import net.quepierts.thatskyinteractions.client.render.BloomBufferSource;
import net.quepierts.thatskyinteractions.client.util.RayTraceUtil;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;

@OnlyIn(Dist.CLIENT)
public class HaloEffectW2SWidget extends World2ScreenWidget {
    private static final ObjectList<HaloEffectW2SWidget> computeList = new ObjectArrayList<>(32);
    private static ByteBuffer colorByteBuffer;
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
    public void setScreenPos(float x, float y) {
        super.setScreenPos(x, y);

        if (this.isInScreen()) {
            computeLater(this);
        }
    }

    @Override
    public boolean shouldRender() {
        if (!super.shouldRender()) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        Entity camera = minecraft.getCameraEntity();

        if (camera == null)
            return false;

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

    private static void computeLater(HaloEffectW2SWidget widget) {
        computeList.add(widget);
    }

    private static void computeShouldRender() {
        if (computeList.isEmpty())
            return;

        RenderTarget renderTarget = PostEffects.getBloomTarget();
        if (renderTarget == null)
            return;

        Minecraft minecraft = Minecraft.getInstance();

        Window window = minecraft.getWindow();
        float xFactor = (float) window.getScreenWidth() / window.getGuiScaledWidth();
        float yFactor = (float) window.getScreenHeight() / window.getGuiScaledHeight();

        int width = renderTarget.width;
        int height = renderTarget.height;
        int capacity = width * height * 4;

        if (colorByteBuffer == null || colorByteBuffer.capacity() < capacity) {
            colorByteBuffer = BufferUtils.createByteBuffer(capacity);
        }

//        RenderUtils.bloomBlit(renderTarget, width, height);

        //renderTarget.blitToScreen(width / 2, height / 2);
        //renderTarget.bindWrite(true);

        renderTarget.bindWrite(true);
        RenderSystem.bindTexture(renderTarget.getColorTextureId());
        GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, colorByteBuffer);
        minecraft.getMainRenderTarget().bindWrite(true);

        for (HaloEffectW2SWidget widget : computeList) {
            int x = (int) (width - widget.x * xFactor);
            int y = (int) (height - widget.y * yFactor);

            if (x < 0 || y < 0)
                continue;

            int index = (x + y * width) * 4;
            if (index < capacity) {
                int read = colorByteBuffer.get(index) & 0xFF;
            }
        }

        computeList.clear();
    }
}
