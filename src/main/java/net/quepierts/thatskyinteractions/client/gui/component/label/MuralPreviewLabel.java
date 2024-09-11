package net.quepierts.thatskyinteractions.client.gui.component.label;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.block.entity.MuralBlockEntity;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.gui.animate.AnimateUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.LerpNumberAnimation;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.component.TEditBox;
import net.quepierts.thatskyinteractions.client.gui.holder.FloatHolder;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector2i;
import org.joml.Vector3i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class MuralPreviewLabel extends TransparentLabel {
    private static final ResourceLocation PLACEHOLDER = ThatSkyInteractions.getLocation("textures/gui/placeholder.png");
    private static final ResourceLocation ARROW = ThatSkyInteractions.getLocation("textures/gui/arrow.png");
    private final Consumer<Vector3i> offsetConsumer;
    private final Consumer<Vector3i> rotateConsumer;
    private final Consumer<Vector2i> sizeConsumer;

    private final Vector3i offsetVec = new Vector3i();
    private final Vector3i rotateVec = new Vector3i();
    private final Vector2i sizeVec = new Vector2i();

    private final TEditBox location;
    private final FloatHolder scale;
    private final FloatHolder rotate;
    private final LerpNumberAnimation scaleAnimation;
    private final LerpNumberAnimation rotateAnimation;
    private ResourceLocation texture;

    public MuralPreviewLabel(ScreenAnimator animator, int xPos, int yPos, int width, int height, MuralBlockEntity mural, Consumer<Vector3i> offsetConsumer, Consumer<Vector3i> rotateConsumer, Consumer<Vector2i> sizeConsumer) {
        super(Component.literal("Preview"), xPos, yPos, width, height, animator);

        this.offsetConsumer = offsetConsumer;
        this.rotateConsumer = rotateConsumer;
        this.sizeConsumer = sizeConsumer;

        this.texture = mural.getTextureLocation();

        this.location = new TEditBox(this.minecraft.font, xPos + 10, yPos + height - 40, width - 20, 20, Component.empty());
        this.location.setMaxLength(128);
        this.location.setValue(this.texture.toString());
        this.location.setConfirm(this::applyResourceLocation);
        this.addWidgets(this.location);

        this.scale = new FloatHolder(1f);
        this.rotate = new FloatHolder(0f);
        this.scaleAnimation = new LerpNumberAnimation(this.scale, AnimateUtils.Lerp::smooth, 0, 0, 0.3f);
        this.rotateAnimation = new LerpNumberAnimation(this.rotate, AnimateUtils.Lerp::smooth, 0, 0, 0.3f);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        PoseStack pose = guiGraphics.pose();

        this.offsetConsumer.accept(this.offsetVec);
        this.rotateConsumer.accept(this.rotateVec);
        this.sizeConsumer.accept(this.sizeVec);

        final int xHalf = this.sizeVec.x / 2;
        final int yHalf = this.sizeVec.y / 2;
        final int xMid = this.getWidth() / 2;
        final int yMid = this.getHeight() / 2;

        float yRot = 0;
        LocalPlayer player = this.minecraft.player;
        if (player != null) {
            yRot = player.getYHeadRot();
        }

        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11.GL_GREATER);
        double guiScale = this.minecraft.getWindow().getGuiScale();
        double windowHeight = this.minecraft.getWindow().getHeight();
        RenderSystem.enableScissor((int)((double)this.getX() * guiScale), (int)(windowHeight - (double)(this.getY() + this.getHeight() - 48) * guiScale), (int)((double)this.width * guiScale), (int)((double)(this.height - 72) * guiScale));
        pose.pushPose();
        pose.translate(this.getX() + xMid, this.getY() + yMid, -500);
        pose.mulPose(Axis.XP.rotationDegrees(30f));
        pose.mulPose(Axis.YP.rotationDegrees(-yRot + 180 + this.rotate.getValue() * Mth.RAD_TO_DEG));

        pose.pushPose();
        pose.translate(-8f, 0f, -8f);

        float scale = this.scale.getValue();
        RenderUtils.blitXZ(pose, PLACEHOLDER, -48, -48, 112, 112, 4 / scale);

        float alpha = Palette.getShaderAlpha();

        pose.translate(8, -1, 8);
        RenderSystem.setShaderColor(0, 0, 1, alpha);
        RenderUtils.blitXZ(pose, ARROW, -8, -56, 16, 16);
        pose.mulPose(Axis.YN.rotation(Mth.HALF_PI));
        RenderSystem.setShaderColor(1, 0, 0, alpha);
        RenderUtils.blitXZ(pose, ARROW, -8, -56, 16, 16);
        RenderSystem.setShaderColor(1, 1, 1, alpha);
        pose.popPose();

        pose.scale(scale, scale, scale);
        pose.translate(this.offsetVec.x, -this.offsetVec.y - 8f, -this.offsetVec.z);
        pose.mulPose(
                new Quaternionf().rotateYXZ(
                        this.rotateVec.y * -Mth.DEG_TO_RAD,
                        this.rotateVec.x * Mth.DEG_TO_RAD,
                        this.rotateVec.z * -Mth.DEG_TO_RAD
                )
        );

        RenderUtils.blit(pose, this.texture, -xHalf, -yHalf, this.sizeVec.x, this.sizeVec.y);
        pose.popPose();
        RenderSystem.disableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableScissor();
        RenderSystem.disableBlend();
    }

    @Override
    public void onResize(int xPos, int yPos, int width, int height) {
        this.setRectangle(width, height, xPos, yPos);
        this.location.setRectangle(width - 20, 20, xPos + 10, yPos + height - 40);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        double delta = scrollY / 2;
        if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_CONTROL)) {
            this.rotateAnimation.reset(this.rotate.get(), this.rotate.get() + delta);
            this.animator.play(this.rotateAnimation);
        } else {
            double dest = Mth.clamp(this.scale.get() + delta, 0.25, 2);

            if (dest != this.scaleAnimation.getDest()) {
                this.scaleAnimation.reset(this.scale.get(), dest);
                this.animator.play(this.scaleAnimation);
            }
        }
        return true;
    }

    private boolean applyResourceLocation(String str) {
        ResourceLocation parse = ResourceLocation.tryParse(str);

        if (parse == null) {
            return false;
        }

        AbstractTexture texture = this.minecraft.getTextureManager().getTexture(parse);
        if (texture.getId() == -1) {
            return false;
        }

        this.texture = parse;
        return true;
    }

    public ResourceLocation getTexture() {
        return texture;
    }
}
