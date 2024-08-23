package net.quepierts.thatskyinteractions.client.gui.component.astrolabe;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.RenderUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.component.CulledRenderable;
import net.quepierts.thatskyinteractions.client.gui.component.LayoutObject;
import net.quepierts.thatskyinteractions.client.gui.holder.FloatHolder;

@OnlyIn(Dist.CLIENT)
public class StarLight extends LayoutObject implements CulledRenderable {
    private final AstrolabeWidget parent;
    private final FloatHolder intensity;
    private final float rand;
    public StarLight(int xPos, int yPos, AstrolabeWidget parent, FloatHolder intensity) {
        super(xPos, yPos, 12, 12);
        this.parent = parent;
        this.intensity = intensity;
        this.rand = ThatSkyInteractions.RANDOM.nextFloat();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int i1, float v) {
        RenderSystem.enableBlend();

        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(this.getX(), this.getY(), 0.0f);
        pose.mulPose(Axis.ZN.rotationDegrees(parent.getRotate()));
        pose.translate(-6, -6, 0);
        RenderUtils.drawCrossHalo(
                guiGraphics, 0, 0, 12,
                0.48f + Mth.sin((rand + ScreenAnimator.GLOBAL.time()) * 12) * 0.02f + intensity.getValue() * 0.5f,
                0xffa4e5f7);
        pose.popPose();
    }
}
