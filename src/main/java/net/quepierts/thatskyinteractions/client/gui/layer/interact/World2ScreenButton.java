package net.quepierts.thatskyinteractions.client.gui.layer.interact;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.RenderUtils;
import org.joml.Vector2f;
import org.joml.Vector3f;

public abstract class World2ScreenButton {
    protected static final ResourceLocation TEXTURE_NORMAL = ThatSkyInteractions.getLocation("textures/gui/w2s_button_normal.png");
    protected static final ResourceLocation TEXTURE_HIGHLIGHT = ThatSkyInteractions.getLocation("textures/gui/w2s_button_highlight.png");
    private final ResourceLocation icon;
    public float xO;
    public float x;
    public float yO;
    public float y;
    public float fade;

    protected World2ScreenButton(ResourceLocation icon) {
        this.icon = icon;
    }

    public void render(GuiGraphics guiGraphics, boolean highlight, float value) {
        PoseStack pose = guiGraphics.pose();

        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        pose.pushPose();
        pose.translate(xO, yO, 100.0f);
        pose.scale(fade, fade, 1.0f);
        pose.mulPose(Axis.YP.rotation(value * Mth.TWO_PI));
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, fade);
        RenderUtils.blit(guiGraphics, highlight ? TEXTURE_HIGHLIGHT : TEXTURE_NORMAL, -16, -16, 32, 32);
        RenderUtils.blit(guiGraphics, icon, -12, -12, 24, 24);

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        pose.popPose();
    }

    public abstract void invoke();

    public abstract Vector3f getWorldPos();

    public boolean shouldRemove() {
        return false;
    }

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
