package net.quepierts.thatskyinteractions.client.gui.component.w2s;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.gui.animate.AnimateUtils;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static net.quepierts.thatskyinteractions.client.gui.layer.World2ScreenWidgetLayer.FADE_BEGIN_DISTANCE;
import static net.quepierts.thatskyinteractions.client.gui.layer.World2ScreenWidgetLayer.FADE_DISTANCE;

@OnlyIn(Dist.CLIENT)
public abstract class World2ScreenWidget {
    protected static final ResourceLocation TEXTURE_NORMAL = ThatSkyInteractions.getLocation("textures/gui/w2s_button_normal.png");
    protected static final ResourceLocation TEXTURE_HIGHLIGHT = ThatSkyInteractions.getLocation("textures/gui/w2s_button_highlight.png");
    protected final Vector3f worldPos = new Vector3f();
    public float xO;
    public float x;
    public float yO;
    public float y;
    public float scale;
    public boolean selectable = false;
    protected boolean limitInScreen = false;
    protected boolean smoothPosition = false;
    private boolean computed = false;
    private boolean inScreen = false;

    protected World2ScreenWidget() {
    }

    public abstract void render(GuiGraphics guiGraphics, boolean highlight, float value, float deltaTicks);

    public abstract void getWorldPos(Vector3f out);

    public void calculateRenderScale(float distance) {
        this.scale = (float) AnimateUtils.Lerp.smooth(0, 1, 1.0f - Math.max(distance - FADE_BEGIN_DISTANCE, 0) / FADE_DISTANCE);
    }

    public void invoke() {}

    public boolean shouldRemove() {
        return false;
    }

    public boolean collided(World2ScreenWidget onGrid) {
        return Vector2f.distanceSquared(this.x, this.y, onGrid.x, onGrid.y) < 32 * 32;
    }

    public void moveIfOverlapped(World2ScreenWidget other) {
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

    public void setComputed() {
        this.computed = true;
    }

    public boolean isComputed() {
        return computed;
    }

    public boolean limitInScreen() {
        return this.limitInScreen;
    }

    public boolean shouldRender() {
        return this.inScreen && this.scale > 0f;
    }

    public boolean shouldSmoothPosition() {
        return this.smoothPosition;
    }

    public boolean shouldSkip() {
        return false;
    }

    public void setInScreen(boolean inScreen) {
        this.inScreen = inScreen;
    }

    public boolean isInScreen() {
        return inScreen;
    }
}
