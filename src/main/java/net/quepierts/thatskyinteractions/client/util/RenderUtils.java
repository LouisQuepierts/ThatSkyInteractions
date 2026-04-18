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
import net.quepierts.thatskyinteractions.client.gui.SdfGraphics;
import net.quepierts.thatskyinteractions.client.reference.Shaders;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL30C;

import java.util.Objects;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class RenderUtils {
    public static final ResourceLocation DEFAULT_ICON = ThatSkyInteractions.getLocation("textures/icon/none.png");

    public static ResourceLocation getInteractionIcon(ResourceLocation interaction) {
        return ResourceLocation.fromNamespaceAndPath(interaction.getNamespace(), "textures/icon/interaction/" + interaction.getPath() + ".png");
    }

    public static void fillRoundRect(GuiGraphics graphics, float x, float y, float width, float height, float radius, int color) {
        SdfGraphics .getInstance()
                    .color(color)
                    .round(radius)
                    .rectangle(x, y, width, height)
                    .fill(graphics.pose());
    }

    public static void fillRoundHTab(GuiGraphics graphics, float x, float y, float width, float height, int color) {
        SdfGraphics .getInstance()
                    .color(color)
                    .roundedHBar(x, y, width, height)
                    .fill(graphics.pose());
    }

    public static void fillCircle(GuiGraphics graphics, float x, float y, int radius, int color) {
        /*float x2 = x + radius * 2;
        float y2 = y + radius * 2;

        Shaders.CIRCLE.use();
        quadIdentity(graphics, x, y, x2, y2, color);*/

        SdfGraphics .getInstance()
                    .color(color)
                    .circle(x, y, radius)
                    .fill(graphics.pose());
    }

    public static void drawRing(GuiGraphics graphics, int x, int y, int radius, float width, int color) {
        /*int x2 = x + radius * 2;
        int y2 = y + radius * 2;

        ShaderInstance shader = Shaders.RING.use();
        shader.safeGetUniform("Width").set(width);

        quadIdentity(graphics, x, y, x2, y2, color);*/

        SdfGraphics .getInstance()
                    .color(color)
                    .circle(x, y, radius)
                    .stroke(graphics.pose(), width);

    }

    public static void drawGlowingRing(GuiGraphics graphics, float x, float y, int radius, float width, int color) {
        RenderSystem.enableBlend();
        /*ShaderInstance shader = Shaders.GLOWING_RING.use();
        Objects.requireNonNull(shader.getUniform("Width")).set(width);

        quadIdentity(graphics, x, y, x2, y2, color);*/

        SdfGraphics .getInstance()
                .center(true)
                .color(color)
                .circle(x, y, radius)
                .stroke(graphics.pose(), width)
                .circle(x, y, radius - width * 0.5f)
                .light(graphics.pose(), 4.0f)
                .reset()
        ;

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
        shaderinstance.safeGetUniform("Blend").set(blend);
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

    private static void sdfQuad(GuiGraphics graphics, float x1, float y1, float w, float h) {
        var x2  = x1 + w;
        var y2  = y1 + h;

        var matrix4f = graphics.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(matrix4f, x1, y1, 0).setUv(0, 0);
        bufferbuilder.addVertex(matrix4f, x1, y2, 0).setUv(0, 1);
        bufferbuilder.addVertex(matrix4f, x2, y2, 0).setUv(1, 1);
        bufferbuilder.addVertex(matrix4f, x2, y1, 0).setUv(1, 0);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }
}
