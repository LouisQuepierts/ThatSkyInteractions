package net.quepierts.thatskyinteractions.client.registry;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;

import java.io.IOException;

public class PostEffects {
    public static final ResourceLocation BLOOM_LOCATION = ThatSkyInteractions.getLocation("shaders/post/bloom.json");
    private static PostChain bloomEffect;
    private static RenderTarget bloomTarget;

    public static void applyWOLBloom(DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget mainRenderTarget = minecraft.getMainRenderTarget();
        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();
        RenderUtils.blitDepth(mainRenderTarget, bloomTarget, width, height);

        bloomTarget.bindWrite(true);
        GlStateManager._glBindFramebuffer(36160, bloomTarget.frameBufferId);
        RenderTypes.getBufferSource().endBatch(RenderTypes.BLOOM);
        //multibuffersource$buffersource.endBatch(RenderTypes.BLOOM);
        bloomEffect.process(deltaTracker.getGameTimeDeltaTicks());
        mainRenderTarget.bindWrite(false);

        /*RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        RenderUtils.bloomBlit(bloomTarget, width, height);
        //bloomTarget.blitToScreen(minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight(), false);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();*/
    }

    public static void doWOLBloom() {
        Minecraft minecraft = Minecraft.getInstance();
        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
//        bloomTarget.blitToScreen(width, height);
        RenderUtils.bloomBlit(bloomTarget, width, height);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public static void processBloom(float partialTick) {
        if (bloomEffect != null) {
            bloomEffect.setUniform("Radius", 1.0f);
            bloomEffect.process(partialTick);
        }
    }

    public static void setup(ResourceProvider provider) {
        Minecraft minecraft = Minecraft.getInstance();
        TextureManager textureManager = minecraft.getTextureManager();

        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();

        if (bloomTarget != null) {
            bloomTarget.destroyBuffers();
        }

        if (bloomEffect != null) {
            bloomEffect.close();
        }

        try {
            bloomEffect = new PostChain(
                    textureManager, provider,
                    minecraft.getMainRenderTarget(),
                    BLOOM_LOCATION
            );
            bloomEffect.resize(width, height);
            bloomTarget = bloomEffect.getTempTarget("final");
        } catch (IOException ioexception) {
            ThatSkyInteractions.LOGGER.warn("Failed to load shader: {}", BLOOM_LOCATION, ioexception);
        } catch (JsonSyntaxException jsonsyntaxexception) {
            ThatSkyInteractions.LOGGER.warn("Failed to parse shader: {}", BLOOM_LOCATION, jsonsyntaxexception);
        }
    }

    public static void resize(int width, int height) {
        if (bloomEffect != null) {
            bloomEffect.resize(width, height);
        }
    }

    public static RenderTarget getBloomTarget() {
        return bloomTarget;
    }
}
