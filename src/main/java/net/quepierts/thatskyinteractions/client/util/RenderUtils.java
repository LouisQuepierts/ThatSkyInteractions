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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.registry.Shaders;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30C;

import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class RenderUtils {
    public static final ResourceLocation DEFAULT_ICON = ThatSkyInteractions.getLocation("textures/icon/none.png");

    public static ResourceLocation getInteractionIcon(ResourceLocation interaction) {
        return ResourceLocation.fromNamespaceAndPath(interaction.getNamespace(), "textures/icon/interaction/" + interaction.getPath() + ".png");
    }

    public static void fillRoundRect(GuiGraphics guiGraphics, int x, int y, int width, int height, float radius, int color) {
        int x2 = x + width;
        int y2 = y + height;

        final float ratio = (float) height / (float) width;

        RenderSystem.setShader(Shaders::getRoundRectShader);
        ShaderInstance shader = Shaders.getRoundRectShader();
        Objects.requireNonNull(shader.getUniform("Ratio")).set(ratio);
        Objects.requireNonNull(shader.getUniform("Radius")).set(radius);

        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.addVertex(matrix4f, (float)x, (float)y, 0).setUv(0, 0).setColor(color);
        bufferbuilder.addVertex(matrix4f, (float)x, (float)y2, 0).setUv(0, 1).setColor(color);
        bufferbuilder.addVertex(matrix4f, (float)x2, (float)y2, 0).setUv(1, 1).setColor(color);
        bufferbuilder.addVertex(matrix4f, (float)x2, (float)y, 0).setUv(1, 0).setColor(color);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }

    public static void drawRing(GuiGraphics guiGraphics, int x, int y, int radius, float width, int color) {
        int x2 = x + radius * 2;
        int y2 = y + radius * 2;

        RenderSystem.setShader(Shaders::getRingShader);
        ShaderInstance shader = Shaders.getRingShader();
        Objects.requireNonNull(shader.getUniform("Width")).set(width);

        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.addVertex(matrix4f, (float)x, (float)y, 0).setUv(0, 0).setColor(color);
        bufferbuilder.addVertex(matrix4f, (float)x, (float)y2, 0).setUv(0, 1).setColor(color);
        bufferbuilder.addVertex(matrix4f, (float)x2, (float)y2, 0).setUv(1, 1).setColor(color);
        bufferbuilder.addVertex(matrix4f, (float)x2, (float)y, 0).setUv(1, 0).setColor(color);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }

    public static void drawGlowingRing(GuiGraphics guiGraphics, int x, int y, int radius, float width, int color) {
        float x1 = x - radius * 0.5f;
        float y1 = y - radius * 0.5f;
        float x2 = x1 + radius * 3;
        float y2 = y1 + radius * 3;

        RenderSystem.enableBlend();
        RenderSystem.setShader(Shaders::getGlowingRingShader);
        ShaderInstance shader = Shaders.getGlowingRingShader();
        Objects.requireNonNull(shader.getUniform("Width")).set(width);

        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.addVertex(matrix4f, x1, y1, 0).setUv(0, 0).setColor(color);
        bufferbuilder.addVertex(matrix4f, x1, y2, 0).setUv(0, 1).setColor(color);
        bufferbuilder.addVertex(matrix4f, x2, y2, 0).setUv(1, 1).setColor(color);
        bufferbuilder.addVertex(matrix4f, x2, y1, 0).setUv(1, 0).setColor(color);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
        RenderSystem.disableBlend();
    }

    public static void drawHalo(GuiGraphics guiGraphics, float x, float y, int size, float intensity, int color) {
        float x2 = x + size;
        float y2 = y + size;

        RenderSystem.enableBlend();
        RenderSystem.setShader(Shaders::getHalo);
        Objects.requireNonNull(Shaders.getHalo().getUniform("Intensity")).set(intensity);

        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.addVertex(matrix4f, x, y, 0).setUv(0, 0).setColor(color);
        bufferbuilder.addVertex(matrix4f, x, y2, 0).setUv(0, 1).setColor(color);
        bufferbuilder.addVertex(matrix4f, x2, y2, 0).setUv(1, 1).setColor(color);
        bufferbuilder.addVertex(matrix4f, x2, y, 0).setUv(1, 0).setColor(color);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
        RenderSystem.disableBlend();
    }

    public static void drawCrossLightSpot(GuiGraphics guiGraphics, float x, float y, int size, float intensity, float width, int color) {
        float x2 = x + size;
        float y2 = y + size;

        RenderSystem.enableBlend();
        RenderSystem.setShader(Shaders::getCrossLightSpotShader);
        ShaderInstance shader = Shaders.getCrossLightSpotShader();
        Objects.requireNonNull(shader.getUniform("Intensity")).set(intensity);
        Objects.requireNonNull(shader.getUniform("Width")).set(width / size);

        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.addVertex(matrix4f, x, y, 0).setUv(0, 0).setColor(color);
        bufferbuilder.addVertex(matrix4f, x, y2, 0).setUv(0, 1).setColor(color);
        bufferbuilder.addVertex(matrix4f, x2, y2, 0).setUv(1, 1).setColor(color);
        bufferbuilder.addVertex(matrix4f, x2, y, 0).setUv(1, 0).setColor(color);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
        RenderSystem.disableBlend();
    }

    public static void drawDoubleCrossHalo(GuiGraphics guiGraphics, float x, float y, int size, float intensity, float width, int color) {
        float x2 = x + size;
        float y2 = y + size;

        RenderSystem.enableBlend();
        RenderSystem.setShader(Shaders::getDoubleCrossLightSpotShader);
        ShaderInstance shader = Shaders.getDoubleCrossLightSpotShader();
        Objects.requireNonNull(shader.getUniform("Intensity")).set(intensity);
        Objects.requireNonNull(shader.getUniform("Width")).set(width / size);

        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.addVertex(matrix4f, x, y, 0).setUv(0, 0).setColor(color);
        bufferbuilder.addVertex(matrix4f, x, y2, 0).setUv(0, 1).setColor(color);
        bufferbuilder.addVertex(matrix4f, x2, y2, 0).setUv(1, 1).setColor(color);
        bufferbuilder.addVertex(matrix4f, x2, y, 0).setUv(1, 0).setColor(color);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
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

    public static void blit(GuiGraphics guiGraphics, ResourceLocation atlasLocation, int x, int y, int width, int height) {
        int x2 = x + width;
        int y2 = y + height;

        RenderSystem.setShaderTexture(0, atlasLocation);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(matrix4f, (float)x, (float)y, 0).setUv(0, 0);
        bufferbuilder.addVertex(matrix4f, (float)x, (float)y2, 0).setUv(0, 1);
        bufferbuilder.addVertex(matrix4f, (float)x2, (float)y2, 0).setUv(1, 1);
        bufferbuilder.addVertex(matrix4f, (float)x2, (float)y, 0).setUv(1, 0);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }

    public static void blitIcon(GuiGraphics guiGraphics, ResourceLocation atlasLocation, int x, int y, int width, int height) {
        int x2 = x + width;
        int y2 = y + height;

        RenderSystem.setShaderTexture(0, getIconTexture(atlasLocation).getId());
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(matrix4f, (float)x, (float)y, 0).setUv(0, 0);
        bufferbuilder.addVertex(matrix4f, (float)x, (float)y2, 0).setUv(0, 1);
        bufferbuilder.addVertex(matrix4f, (float)x2, (float)y2, 0).setUv(1, 1);
        bufferbuilder.addVertex(matrix4f, (float)x2, (float)y, 0).setUv(1, 0);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }

    private static void innerBlit(GuiGraphics guiGraphics, ResourceLocation atlasLocation, int x1, int x2, int y1, int y2, int blitOffset, float minU, float maxU, float minV, float maxV) {
        RenderSystem.setShaderTexture(0, getIconTexture(atlasLocation).getId());
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = guiGraphics.pose().last().pose();
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

    public static void bloomBlit(RenderTarget target, int width, int height) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._colorMask(true, true, true, false);
        GlStateManager._disableDepthTest();
        GlStateManager._depthMask(false);
        GlStateManager._viewport(0, 0, width, height);

        ShaderInstance shaderinstance = Shaders.getBloomBlit();
        shaderinstance.setSampler("ScreenSampler", Minecraft.getInstance().getMainRenderTarget().getColorTextureId());
        shaderinstance.setSampler("DiffuseSampler", target.getColorTextureId());
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
    }

    public static void blitDepth(RenderTarget src, RenderTarget dest, int width, int height) {
        GL30C.glBindFramebuffer(GL30C.GL_READ_FRAMEBUFFER, src.frameBufferId);
        GL30C.glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, dest.frameBufferId);
        GL30C.glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL30C.GL_DEPTH_BUFFER_BIT, GL30C.GL_NEAREST);

        GL30C.glBindFramebuffer(GL30C.GL_FRAMEBUFFER, 0);
    }
}
