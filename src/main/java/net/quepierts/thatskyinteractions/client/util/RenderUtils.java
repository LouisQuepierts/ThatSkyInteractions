package net.quepierts.thatskyinteractions.client.util;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.reference.Shaders;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30C;

import java.util.Objects;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class RenderUtils {
    public static final ResourceLocation DEFAULT_ICON = ThatSkyInteractions.getLocation("textures/icon/none.png");

    public static ResourceLocation getInteractionIcon(ResourceLocation interaction) {
        return ResourceLocation.fromNamespaceAndPath(interaction.getNamespace(), "textures/icon/interaction/" + interaction.getPath() + ".png");
    }

    public static void frameRoundRect(GuiGraphics graphics, float x, float y, float width, float height, float lineRadius, float rectRadius, int color) {
        float x2 = x + width;
        float y2 = y + height;

        final float shorter = Math.min(width, height);
        final float xRatio = Math.max(width / height, 1.0f);
        final float yRatio = Math.max(height / width, 1.0f);
        final float nRectRadius = Math.clamp(rectRadius / shorter, 0.0f, 1.0f);
        final float nLineRadius = Math.clamp(lineRadius / shorter, 0.0f, 1.0f);

        RenderSystem.enableBlend();
        ShaderInstance shader = Shaders.ROUND_RECT.use();

        shader.getUniform("Rect").set(xRatio, yRatio);
        shader.getUniform("Radii").set(nRectRadius, nLineRadius);
        shader.getUniform("Smooth").set(0.5f / shorter);
        
        quadIdentity(graphics, x, y, x2, y2, color);
        RenderSystem.disableBlend();
    }

    public static void fillRoundRect(GuiGraphics graphics, float x, float y, float width, float height, float radius, int color) {
        float x2 = x + width;
        float y2 = y + height;

        final float shorter = Math.min(width, height);
        final float xRatio = Math.max(width / height, 1.0f);
        final float yRatio = Math.max(height / width, 1.0f);
        final float nRectRadius = Math.clamp(radius / shorter, 0.0f, 1.0f);

        ShaderInstance shader = Shaders.ROUND_RECT.use();
        shader.getUniform("Rect").set(xRatio, yRatio);
        shader.getUniform("Radius").set(nRectRadius);
        shader.getUniform("Smooth").set(0.5f / shorter);

        quadIdentity(graphics, x, y, x2, y2, color);
    }

    public static void fillSector(GuiGraphics graphics, float x, float y, float scale, float sweepAngle, float middleAngle, float circleRadius, float sectorRadius, float edgeRadius, int color) {
        float x2 = x + scale;
        float y2 = y + scale;
        float nOuterRadius = Math.clamp(circleRadius / scale, 0.0f, 1.0f);
        float nInnerRadius = Math.clamp(sectorRadius / scale, 0.0f, 1.0f);
        float nEdgeRadius = Math.clamp(edgeRadius / scale, 0.0f, 1.0f);

        RenderSystem.enableBlend();

        ShaderInstance shader = Shaders.SECTOR.use();
        shader.getUniform("Radians").set(sweepAngle * Mth.DEG_TO_RAD, middleAngle * Mth.DEG_TO_RAD);
        shader.getUniform("Radii").set(nOuterRadius, nInnerRadius, nEdgeRadius);
        shader.getUniform("Smooth").set(0.5f / scale);

        quadIdentity(graphics, x, y, x2, y2, color);

        RenderSystem.disableBlend();
    }

    public static void drawSectorStroke(GuiGraphics graphics, float x, float y, float scale, float sweepAngle, float middleAngle, float circleRadius, float sectorRadius, float edgeRadius, float stroke, int color) {
        float x2 = x + scale;
        float y2 = y + scale;
        float nOuterRadius = Math.clamp(circleRadius / scale, 0.0f, 1.0f);
        float nInnerRadius = Math.clamp(sectorRadius / scale, 0.0f, 1.0f);
        float nEdgeRadius = Math.clamp(edgeRadius / scale, 0.0f, 1.0f);
        float nStroke = Math.clamp(stroke / scale, 0.0f, 1.0f);

        RenderSystem.enableBlend();

        ShaderInstance shader = Shaders.SECTOR_STROKE.use();
        shader.getUniform("Radians").set(sweepAngle * Mth.DEG_TO_RAD, middleAngle * Mth.DEG_TO_RAD);
        shader.getUniform("Radii").set(nOuterRadius, nInnerRadius, nEdgeRadius);
        shader.getUniform("Stroke").set(nStroke);
        shader.getUniform("Smooth").set(0.5f / scale);

        quadIdentity(graphics, x, y, x2, y2, color);

        RenderSystem.disableBlend();
    }

    public static void fillCircle(GuiGraphics graphics, float x, float y, int radius, int color) {
        float x2 = x + radius * 2;
        float y2 = y + radius * 2;

        Shaders.CIRCLE.use();
        quadIdentity(graphics, x, y, x2, y2, color);
    }

    public static void drawRing(GuiGraphics graphics, int x, int y, int radius, float width, int color) {
        int x2 = x + radius * 2;
        int y2 = y + radius * 2;

        ShaderInstance shader = Shaders.RING.use();
        shader.safeGetUniform("Width").set(width);

        quadIdentity(graphics, x, y, x2, y2, color);
    }

    public static void drawGlowingRing(GuiGraphics graphics, float x, float y, int radius, float width, int color) {
        float x1 = x - radius * 0.5f;
        float y1 = y - radius * 0.5f;
        float x2 = x1 + radius * 3;
        float y2 = y1 + radius * 3;

        RenderSystem.enableBlend();
        ShaderInstance shader = Shaders.GLOWING_RING.use();
        Objects.requireNonNull(shader.getUniform("Width")).set(width);

        quadIdentity(graphics, x, y, x2, y2, color);
        RenderSystem.disableBlend();
    }

    public static void drawHalo(GuiGraphics graphics, float x, float y, int size, float intensity, int color) {
        float x2 = x + size;
        float y2 = y + size;

        RenderSystem.enableBlend();
        ShaderInstance shader = Shaders.HALO.use();
        Objects.requireNonNull(shader.getUniform("Intensity")).set(intensity);

        quadIdentity(graphics, x, y, x2, y2, color);
        RenderSystem.disableBlend();
    }

    public static void drawLightSpot(GuiGraphics graphics, float x, float y, int size, float intensity, int color) {
        float x2 = x + size;
        float y2 = y + size;

        RenderSystem.enableBlend();
        ShaderInstance shader = Shaders.LIGHT_SPOT.use();
        Objects.requireNonNull(shader.getUniform("Intensity")).set(intensity);

        quadIdentity(graphics, x, y, x2, y2, color);
        RenderSystem.disableBlend();
    }

    public static void drawCrossLightSpot(GuiGraphics graphics, float x, float y, int size, float intensity, float width, int color) {
        float x2 = x + size;
        float y2 = y + size;

        RenderSystem.enableBlend();
        ShaderInstance shader = Shaders.CROSS_LIGHT_SPOT.use();
        Objects.requireNonNull(shader.getUniform("Intensity")).set(intensity);
        Objects.requireNonNull(shader.getUniform("Width")).set(width / size);

        quadIdentity(graphics, x, y, x2, y2, color);
        RenderSystem.disableBlend();
    }

    public static void drawDoubleCrossHalo(GuiGraphics graphics, float x, float y, int size, float intensity, float width, int color) {
        float x2 = x + size;
        float y2 = y + size;

        RenderSystem.enableBlend();
        ShaderInstance shader = Shaders.DOUBLE_CROSS_LIGHT_SPOT.use();
        Objects.requireNonNull(shader.getUniform("Intensity")).set(intensity);
        Objects.requireNonNull(shader.getUniform("Width")).set(width / size);

        quadIdentity(graphics, x, y, x2, y2, color);
        RenderSystem.disableBlend();
    }

    public static void blit(PoseStack poseStack, ResourceLocation location, int x, int y, int width, int height) {
        int x2 = x + width;
        int y2 = y + height;

        Matrix4f matrix4f = poseStack.last().pose();
        RenderSystem.setShaderTexture(0, location);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(matrix4f, (float)x, (float)y, 0).setUv(0, 0);
        bufferbuilder.addVertex(matrix4f, (float)x, (float)y2, 0).setUv(0, 1);
        bufferbuilder.addVertex(matrix4f, (float)x2, (float)y2, 0).setUv(1, 1);
        bufferbuilder.addVertex(matrix4f, (float)x2, (float)y, 0).setUv(1, 0);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }

    public static void blitXZ(PoseStack poseStack, ResourceLocation location, int x, int z, int width, int height, float uvScale) {
        int x2 = x + width;
        int y2 = z + height;

        Matrix4f matrix4f = poseStack.last().pose();
        RenderSystem.setShaderTexture(0, location);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(matrix4f, (float)x, 0, (float)z).setUv(-uvScale, -uvScale);
        bufferbuilder.addVertex(matrix4f, (float)x, 0, (float)y2).setUv(-uvScale, uvScale);
        bufferbuilder.addVertex(matrix4f, (float)x2, 0, (float)y2).setUv(uvScale, uvScale);
        bufferbuilder.addVertex(matrix4f, (float)x2, 0, (float)z).setUv(uvScale, -uvScale);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }

    public static void blitXZ(PoseStack poseStack, ResourceLocation location, int x, int z, int width, int height) {
        int x2 = x + width;
        int y2 = z + height;

        Matrix4f matrix4f = poseStack.last().pose();
        RenderSystem.setShaderTexture(0, location);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(matrix4f, (float)x, 0, (float)z).setUv(0, 0);
        bufferbuilder.addVertex(matrix4f, (float)x, 0, (float)y2).setUv(0, 1);
        bufferbuilder.addVertex(matrix4f, (float)x2, 0, (float)y2).setUv(1, 1);
        bufferbuilder.addVertex(matrix4f, (float)x2, 0, (float)z).setUv(1, 0);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }

    public static void blit(GuiGraphics graphics, ResourceLocation atlasLocation, int x, int y, int width, int height) {
        int x2 = x + width;
        int y2 = y + height;

        RenderSystem.setShaderTexture(0, atlasLocation);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = graphics.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(matrix4f, (float)x, (float)y, 0).setUv(0, 0);
        bufferbuilder.addVertex(matrix4f, (float)x, (float)y2, 0).setUv(0, 1);
        bufferbuilder.addVertex(matrix4f, (float)x2, (float)y2, 0).setUv(1, 1);
        bufferbuilder.addVertex(matrix4f, (float)x2, (float)y, 0).setUv(1, 0);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }

    public static void blitIcon(GuiGraphics graphics, ResourceLocation atlasLocation, int x, int y, int width, int height) {
        int x2 = x + width;
        int y2 = y + height;

        RenderSystem.setShaderTexture(0, getIconTexture(atlasLocation).getId());
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = graphics.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(matrix4f, (float)x, (float)y, 0).setUv(0, 0);
        bufferbuilder.addVertex(matrix4f, (float)x, (float)y2, 0).setUv(0, 1);
        bufferbuilder.addVertex(matrix4f, (float)x2, (float)y2, 0).setUv(1, 1);
        bufferbuilder.addVertex(matrix4f, (float)x2, (float)y, 0).setUv(1, 0);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }

    private static void innerBlit(GuiGraphics graphics, ResourceLocation atlasLocation, int x1, int x2, int y1, int y2, int blitOffset, float minU, float maxU, float minV, float maxV) {
        RenderSystem.setShaderTexture(0, getIconTexture(atlasLocation).getId());
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = graphics.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(matrix4f, (float)x1, (float)y1, (float)blitOffset).setUv(minU, minV);
        bufferbuilder.addVertex(matrix4f, (float)x1, (float)y2, (float)blitOffset).setUv(minU, maxV);
        bufferbuilder.addVertex(matrix4f, (float)x2, (float)y2, (float)blitOffset).setUv(maxU, maxV);
        bufferbuilder.addVertex(matrix4f, (float)x2, (float)y1, (float)blitOffset).setUv(maxU, minV);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }

    private static AbstractTexture getIconTexture(ResourceLocation location) {
        TextureManager manager = Minecraft.getInstance().getTextureManager();
        AbstractTexture texture = manager.getTexture(location);
        return texture == MissingTextureAtlasSprite.getTexture() ? manager.getTexture(DEFAULT_ICON) : texture;
    }

    public static void bloomBlit(RenderTarget src, RenderTarget target, int width, int height, float blend) {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);

        RenderSystem.assertOnRenderThread();
        GlStateManager._colorMask(true, true, true, false);
        GlStateManager._disableDepthTest();
        GlStateManager._depthMask(false);
        GlStateManager._viewport(0, 0, width, height);

        ShaderInstance shaderinstance = Shaders.BLOOM_BLIT.getInstance();
        shaderinstance.setSampler("ScreenSampler", target.getColorTextureId());
        shaderinstance.setSampler("DiffuseSampler", src.getColorTextureId());
        Objects.requireNonNull(shaderinstance.getUniform("Blend")).set(blend);
        shaderinstance.apply();
        BufferBuilder bufferbuilder = RenderSystem.renderThreadTesselator().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLIT_SCREEN);
        bufferbuilder.addVertex(0.0F, 0.0F, 0.0F);
        bufferbuilder.addVertex(1.0F, 0.0F, 0.0F);
        bufferbuilder.addVertex(1.0F, 1.0F, 0.0F);
        bufferbuilder.addVertex(0.0F, 1.0F, 0.0F);
        BufferUploader.draw(bufferbuilder.buildOrThrow());
        shaderinstance.clear();
        GlStateManager._depthMask(true);
        GlStateManager._colorMask(true, true, true, true);

        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public static void bloomBlit(RenderTarget target, int width, int height, float blend) {
        bloomBlit(target, Minecraft.getInstance().getMainRenderTarget(), width, height, blend);
    }

    public static void blitDepth(RenderTarget src, RenderTarget dest, int width, int height) {
        GL30C.glBindFramebuffer(GL30C.GL_READ_FRAMEBUFFER, src.frameBufferId);
        GL30C.glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, dest.frameBufferId);
        GL30C.glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL30C.GL_DEPTH_BUFFER_BIT, GL30C.GL_NEAREST);
        GL30C.glBindFramebuffer(GL30C.GL_FRAMEBUFFER, 0);
        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
    }

    private static void quadIdentity(GuiGraphics graphics, float x1, float y1, float x2, float y2) {
        Matrix4f matrix4f = graphics.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(matrix4f, x1, y1, 0).setUv(0, 0);
        bufferbuilder.addVertex(matrix4f, x1, y2, 0).setUv(0, 1);
        bufferbuilder.addVertex(matrix4f, x2, y2, 0).setUv(1, 1);
        bufferbuilder.addVertex(matrix4f, x2, y1, 0).setUv(1, 0);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }

    private static void quadIdentity(GuiGraphics graphics, float x1, float y1, float x2, float y2, int color) {
        Matrix4f matrix4f = graphics.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.addVertex(matrix4f, x1, y1, 0).setUv(0, 0).setColor(color);
        bufferbuilder.addVertex(matrix4f, x1, y2, 0).setUv(0, 1).setColor(color);
        bufferbuilder.addVertex(matrix4f, x2, y2, 0).setUv(1, 1).setColor(color);
        bufferbuilder.addVertex(matrix4f, x2, y1, 0).setUv(1, 0).setColor(color);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }
}
