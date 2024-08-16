package net.quepierts.thatskyinteractions.client.gui.layer.interact;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.gui.RenderUtils;
import org.joml.Vector2f;
import org.joml.Vector3f;

public abstract class World2ScreenButton {
    public static final ResourceLocation TEXTURE_CIRCLE = ThatSkyInteractions.getLocation("textures/gui/circle.png");
    private final ResourceLocation icon;
    public boolean render = false;
    public float xO;
    public float x;
    public float yO;
    public float y;

    protected World2ScreenButton(ResourceLocation icon) {
        this.icon = icon;
    }

    public void render(GuiGraphics guiGraphics, float highlight) {
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(xO, yO, 0.0f);
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 0.75f);
        RenderUtils.blit(guiGraphics, TEXTURE_CIRCLE, -16, -16, 32, 32);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderUtils.blit(guiGraphics, icon, -16, -16, 32, 32);
        RenderSystem.disableBlend();
        pose.popPose();
    }

    public abstract void invoke();

    public abstract Vector3f getWorldPos();

    public boolean collided(World2ScreenButton onGrid) {
        return Vector2f.distanceSquared(this.x, this.y, onGrid.x, onGrid.y) < 32 * 32;
    }

    public void moveIfOverlapped(World2ScreenButton other) {
        float dx = this.x - other.x;
        float dy = this.y - other.y;
        float distance = Mth.sqrt(dx * dx + dy * dy);

        if (distance < 32) {
            float moveDistance = 32 - distance;
            float moveX = (moveDistance / distance) * dx;
            float moveY = (moveDistance / distance) * dy;

            this.x += moveX;
            this.y += moveY;
        }
    }
}
