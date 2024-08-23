package net.quepierts.thatskyinteractions.client.gui.component.astrolabe;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.gui.component.CulledRenderable;
import net.quepierts.thatskyinteractions.client.gui.component.LayoutObject;
import net.quepierts.thatskyinteractions.client.gui.holder.FloatHolder;
import org.joml.Vector2f;
import org.joml.Vector4f;

@OnlyIn(Dist.CLIENT)
public class AstrolabeLine extends LayoutObject implements CulledRenderable {
    public static final ResourceLocation TEXTURE = ThatSkyInteractions.getLocation("textures/gui/glowing_line.png");

    private final Vector2f position;
    private final float rotation;
    private final float length;
    private final FloatHolder alpha;

    public AstrolabeLine(Vector2f position, float length, float rotation, FloatHolder alpha) {
        super((int) position.x, (int) position.y, 16, (int) length);
        this.position = position;
        this.rotation = rotation;
        this.length = length;
        this.alpha = alpha;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int i1, float v) {

        PoseStack pose = guiGraphics.pose();
        float alpha = Palette.getShaderAlpha();
        RenderSystem.setShaderColor(0x25 / 255f, 0x22 / 255f, 0x3d / 255f, alpha * this.alpha.getValue());
        pose.pushPose();
        pose.translate(this.position.x, this.position.y, 0);
        pose.mulPose(Axis.ZP.rotation(this.rotation));
        pose.scale(1.0f, this.length, 1.0f);

        RenderSystem.enableBlend();
        guiGraphics.blit(TEXTURE, -8, 0, 16, 1, 0.0f, 0.0f, 16, 16, 16, 16);
        RenderSystem.disableBlend();
        pose.popPose();

        RenderSystem.setShaderColor(1, 1, 1, alpha);
    }

    @Override
    public boolean shouldRender(Vector4f region) {
        float cos = Mth.cos(this.rotation);
        float height = (this.getHeight() + 16) * cos;
        float y = this.getY() + 32 * cos;
        if (cos < 0) {
            return CulledRenderable.intersects(region, this.getX(), y + height, this.getWidth(), -height);
        } else {
            return CulledRenderable.intersects(region, this.getX(), y, this.getWidth(), height);
        }
    }
}
