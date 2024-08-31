package net.quepierts.thatskyinteractions.client.registry;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
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
    private static RenderTarget bloomFinalTarget;
    private static RenderTarget bloomSurroundTarget;

    private static boolean shouldApplyBloom = false;

    public static void applyWOLBloom(DeltaTracker deltaTracker) {
        if (!shouldApplyBloom)
            return;

        bloomFinalTarget.clear(Minecraft.ON_OSX);
        bloomSurroundTarget.clear(Minecraft.ON_OSX);

        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget mainRenderTarget = minecraft.getMainRenderTarget();
        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();
        RenderUtils.blitDepth(mainRenderTarget, bloomFinalTarget, width, height);

        bloomFinalTarget.bindWrite(false);
        RenderTypes.getBufferSource().endBatch(RenderTypes.WOL);
        /*mainRenderTarget.bindWrite(false);
        RenderUtils.bloomBlit(bloomFinalTarget, width, height, 1.0f);
        bloomFinalTarget.bindWrite(false);*/

        bloomEffect.process(deltaTracker.getGameTimeDeltaTicks());
        mainRenderTarget.bindWrite(false);
    }

    public static void doWOLBloom() {
        if (!shouldApplyBloom)
            return;
        Minecraft minecraft = Minecraft.getInstance();
        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();

        //bloomFinalTarget.blitToScreen(width / 2, height / 2);

        minecraft.getMainRenderTarget().bindWrite(false);
        RenderUtils.bloomBlit(bloomSurroundTarget, width, height, 1.2f);
        RenderUtils.bloomBlit(bloomFinalTarget, width, height, 1.8f);
        shouldApplyBloom = false;
    }

    public static void setApplyBloom() {
        shouldApplyBloom = true;
    }

    public static void setup(ResourceProvider provider) {
        Minecraft minecraft = Minecraft.getInstance();
        TextureManager textureManager = minecraft.getTextureManager();

        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();

        if (bloomFinalTarget != null) {
            bloomFinalTarget.destroyBuffers();
        }

        if (bloomSurroundTarget != null) {
            bloomSurroundTarget.destroyBuffers();
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
            bloomFinalTarget = bloomEffect.getTempTarget("final");
            bloomSurroundTarget = bloomEffect.getTempTarget("surround");
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

    public static RenderTarget getBloomFinalTarget() {
        return bloomFinalTarget;
    }

    public static RenderTarget getBloomSurroundTarget() {
        return bloomSurroundTarget;
    }
}
