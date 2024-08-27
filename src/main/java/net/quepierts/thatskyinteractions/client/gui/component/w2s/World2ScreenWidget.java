package net.quepierts.thatskyinteractions.client.gui.component.w2s;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import org.joml.Vector2f;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public abstract class World2ScreenWidget {
    protected static final ResourceLocation TEXTURE_NORMAL = ThatSkyInteractions.getLocation("textures/gui/w2s_button_normal.png");
    protected static final ResourceLocation TEXTURE_HIGHLIGHT = ThatSkyInteractions.getLocation("textures/gui/w2s_button_highlight.png");
    public float xO;
    public float x;
    public float yO;
    public float y;
    public float fade;
    public boolean selectable = false;
    private boolean computed = false;

    protected World2ScreenWidget() {
    }

    public abstract void render(GuiGraphics guiGraphics, boolean highlight, float value);

    public abstract void getWorldPos(Vector3f out);

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
}
