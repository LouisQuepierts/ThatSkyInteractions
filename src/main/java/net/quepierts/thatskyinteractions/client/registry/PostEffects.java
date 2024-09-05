package net.quepierts.thatskyinteractions.client.registry;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
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
    public static final ResourceLocation CLOUD_LOCATION = ThatSkyInteractions.getLocation("shaders/post/cloud.json");
    private static PostChain bloomEffect;
    private static PostChain cloudEffect;
    private static RenderTarget bloomFinalTarget;
    private static RenderTarget bloomSurroundTarget;
    private static RenderTarget cloudTarget;

    private static boolean shouldApplyBloom = false;

    public static void prepareBloom() {
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
        mainRenderTarget.bindWrite(false);
    }

    public static void postBloom(float deltaTracker) {
        bloomFinalTarget.bindWrite(false);
        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget mainRenderTarget = minecraft.getMainRenderTarget();
        //RenderUtils.blitDepth(bloomFinalTarget, mainRenderTarget, minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight());

        bloomEffect.process(deltaTracker);
        mainRenderTarget.bindWrite(false);
        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();

        //bloomFinalTarget.blitToScreen(width / 2, height / 2);

        minecraft.getMainRenderTarget().bindWrite(false);
        RenderUtils.bloomBlit(bloomSurroundTarget, width, height, 1.2f);
        RenderUtils.bloomBlit(bloomFinalTarget, width, height, 1.8f);
//        bloomFinalTarget.blitToScreen(width, height);
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

        if (cloudEffect != null) {
            cloudEffect.close();
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

        try {
            cloudEffect = new PostChain(
                    textureManager, provider,
                    minecraft.getMainRenderTarget(),
                    CLOUD_LOCATION
            );
            cloudEffect.resize(width, height);
            cloudTarget = cloudEffect.getTempTarget("final");
        }catch (IOException ioexception) {
            ThatSkyInteractions.LOGGER.warn("Failed to load shader: {}", CLOUD_LOCATION, ioexception);
        } catch (JsonSyntaxException jsonsyntaxexception) {
            ThatSkyInteractions.LOGGER.warn("Failed to parse shader: {}", CLOUD_LOCATION, jsonsyntaxexception);
        }
    }

    public static void resize(int width, int height) {
        if (bloomEffect != null) {
            bloomEffect.resize(width, height);
        }

        if (cloudEffect != null) {
            cloudEffect.resize(width, height);
        }
    }

    public static RenderTarget getBloomFinalTarget() {
        return bloomFinalTarget;
    }

    public static RenderTarget getBloomSurroundTarget() {
        return bloomSurroundTarget;
    }

    public static RenderTarget getCloudTarget() {
        return cloudTarget;
    }

    public static PostChain getCloudEffect() {
        return cloudEffect;
    }

    public static boolean shouldApplyBloom() {
        return shouldApplyBloom;
    }
}
