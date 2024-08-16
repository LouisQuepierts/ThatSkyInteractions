package net.quepierts.thatskyinteractions.client.gui.component;

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
import net.quepierts.thatskyinteractions.client.gui.animate.AnimateUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.LerpNumberAnimation;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.holder.DoubleHolder;
import net.quepierts.thatskyinteractions.data.tree.NodeState;
import org.joml.Vector4f;

@OnlyIn(Dist.CLIENT)
public class GlowingLine extends LayoutObject implements CulledRenderable {
    public static final ResourceLocation TEXTURE = ThatSkyInteractions.getLocation("textures/gui/glowing_line.png");

    private final ScreenAnimator animator;
    private final NodeState type;
    private final float rotation;
    private final DoubleHolder length;

    private boolean load = false;

    public GlowingLine(int x, int y, ScreenAnimator animator, int length, NodeState type, float rotation) {
        super(x, y, 16, length);
        this.animator = animator;
        this.type = type;
        this.rotation = rotation * Mth.DEG_TO_RAD;
        this.length = new DoubleHolder(0.0f);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int i1, float v) {
        if (!this.load) {
            animator.play(new LerpNumberAnimation(length, AnimateUtils.Lerp::smooth, 0.0, this.getHeight(), 0.5f), 0.5f);
            this.load = true;
        }

        Palette.useColor(this.type);
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(this.getX() + 7.5f, this.getY(), 0);
        pose.mulPose(Axis.ZP.rotation(this.rotation));
        pose.translate(-8.0f, 32.0f, 0.0f);
        pose.scale(1.0f, (float) this.length.get(), 1.0f);

        RenderSystem.enableBlend();
        guiGraphics.blit(TEXTURE, 0, 0, 16, 1, 0.0f, 0.0f, 16, 16, 16, 16);
        RenderSystem.disableBlend();
        pose.popPose();

        Palette.reset();
    }

    public DoubleHolder getLength() {
        return length;
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
