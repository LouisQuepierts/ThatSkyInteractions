package net.quepierts.thatskyinteractions.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.CameraHandler;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.gui.animate.AnimateUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.LerpNumberAnimation;
import net.quepierts.thatskyinteractions.client.gui.component.astrolabe.AstrolabeWidget;
import net.quepierts.thatskyinteractions.client.gui.holder.FloatHolder;
import net.quepierts.thatskyinteractions.data.astrolabe.Astrolabe;
import net.quepierts.thatskyinteractions.data.astrolabe.AstrolabeManager;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class FriendAstrolabeScreen extends AnimatedScreen {
    private static final ResourceLocation ASTROLABE_BESTIES = ThatSkyInteractions.getLocation("besties");
    private static final ResourceLocation ASTROLABE_BESTIES_2 = ThatSkyInteractions.getLocation("besties_2");
    private int index = 2;
    private final CameraHandler cameraHandler;
    private final AstrolabeWidget[] astrolabes = new AstrolabeWidget[5];
    private final FloatHolder closerHolder = new FloatHolder(0.7f);
    private final LerpNumberAnimation closerAnimation = new LerpNumberAnimation(closerHolder, AnimateUtils.Lerp::smooth, 0, 0, 1.0f);
    public FriendAstrolabeScreen() {
        super(Component.empty());
        this.cameraHandler = ThatSkyInteractions.getInstance().getClient().getCameraHandler();
    }

    @Override
    protected void init() {
        this.astrolabes[2] = new AstrolabeWidget(this, 0, 0);
        this.astrolabes[3] = new AstrolabeWidget(this, 0, 0);
        AstrolabeManager astrolabeManager = ThatSkyInteractions.getInstance().getProxy().getAstrolabeManager();
        Astrolabe besties1 = astrolabeManager.get(ASTROLABE_BESTIES);
        if (besties1 != null) {
            this.astrolabes[2].reset(besties1);
            this.astrolabes[2].enter();
        }

        Astrolabe besties2 = astrolabeManager.get(ASTROLABE_BESTIES_2);
        if (besties2 != null) {
            this.astrolabes[3].reset(besties2);
        }
    }

    @Override
    public void enter() {
        super.enter();
        Vector3f unmodifiedRotation = cameraHandler.getUnmodifiedRotation();
        //Vector3f unmodifiedSkyColor = cameraHandler.getUnmodifiedSkyColor();
        float unmodifiedDayTime = cameraHandler.getUnmodifiedDayTime();

        cameraHandler.rotateTo(new Vector3f(-45 - unmodifiedRotation.x, 0, 0));
        cameraHandler.dayTimeTo(0.5f - unmodifiedDayTime);
        this.closerAnimation.reset(0.7f, 1.0f);
        this.animator.play(this.closerAnimation, 0.2f);
    }

    @Override
    public void hide() {
        super.hide();
        cameraHandler.resetRotation();
        cameraHandler.resetDayTime();
        this.closerAnimation.reset(1.0f, 0.5f);
        this.animator.play(this.closerAnimation, 0.2f);
    }

    @Override
    public void irender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        Vector3f cameraRotation = this.cameraHandler.getRotation();
        float destRotX = this.cameraHandler.getDestRotation().x;

        float xHalf = this.width / 2.0f;
        float yHalf = this.height / 2.0f;

        float alpha = Palette.getShaderAlpha();
        float enterValue = this.enter.getValue();
        float enterY = (1 - enterValue) * Mth.cos((destRotX - cameraRotation.x()) * Mth.DEG_TO_RAD) * 200f;

        int localMouseX = (int) (mouseX - xHalf);
        int localMouseY = (int) (mouseY - yHalf);

        Palette.setShaderAlpha(enterValue);
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(xHalf, yHalf, 0);

        for (Renderable renderable : this.renderables) {
            renderable.render(guiGraphics, localMouseX, localMouseY, partialTick);
        }

        pose.translate(0, -yHalf, 0);

        float yRot = cameraRotation.y;
        float scale = this.closerHolder.getValue();
        pose.scale(scale, scale, 1.0f);

        for (int i = 0; i < this.astrolabes.length; i++) {
            int diff = i - 2;

            if (Mth.abs(diff) > 1)
                continue;

            AstrolabeWidget astrolabe = this.astrolabes[i];
            if (astrolabe == null)
                continue;

            float rot = yRot - diff * 45;
            pose.pushPose();
            pose.translate(0, -this.height, 300);
            pose.mulPose(Axis.ZP.rotationDegrees(rot));
            pose.translate(-rot, this.height + yHalf - enterY, -300);

            astrolabe.renderAstrolabe(guiGraphics, localMouseX, localMouseY, partialTick, rot);

            pose.popPose();
        }

        pose.popPose();
        Palette.setShaderAlpha(alpha);

        RenderSystem.disableDepthTest();
        RenderSystem.enableCull();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        Vector3f target = this.cameraHandler.getDestRotation();
        AstrolabeWidget astrolabe = this.astrolabes[this.index];
        boolean rotated = false;
        switch (keyCode) {
            case GLFW.GLFW_KEY_A:
                if (--this.index < 0) {
                    this.index = 4;
                }
                rotated = true;
                break;
            case GLFW.GLFW_KEY_D:
                this.index++;
                this.index %= 5;
                rotated = true;
                break;
        }

        if (rotated) {
            if (astrolabe != null) {
                astrolabe.hide();
            }
            astrolabe = this.astrolabes[this.index];
            if (astrolabe != null) {
                astrolabe.enter();
            }
            target.y = (this.index - 2) * 45;
            this.cameraHandler.rotateTo(target);
            return true;
        }

        /*if (this.astrolabeWidget.keyPressed(keyCode, scanCode, modifiers))
            return true;*/
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        AstrolabeWidget astrolabe = this.astrolabes[this.index];
        double localMouseX = mouseX - this.width / 2.0;
        double localMouseY = mouseY - this.height / 2.0;

        if (astrolabe != null && astrolabe.mouseClicked(localMouseX, localMouseY, button)) {
            return true;
        }

        return super.mouseClicked(localMouseX, localMouseY, button);
    }
}
