package net.quepierts.thatskyinteractions.client.render.bloom;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.registry.RenderTypes;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;

import java.io.IOException;

public class BloomRenderer {
    public static final ResourceLocation EFFECT_LOCATION = ThatSkyInteractions.getLocation("shaders/post/bloom.json");

    private PostChain effect;
    private RenderTarget finalTarget;
    private RenderTarget surroundTarget;

    private boolean shouldApplyBloom = false;

    public void prepareBloom() {
        if (!this.shouldApplyBloom) {
            return;
        }

        this.finalTarget.clear(Minecraft.ON_OSX);
        this.surroundTarget.clear(Minecraft.ON_OSX);

        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget mainRenderTarget = minecraft.getMainRenderTarget();
        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();
        RenderUtils.blitDepth(mainRenderTarget, this.finalTarget, width, height);

        this.finalTarget.bindWrite(false);
        RenderTypes.getBufferSource().endBatch();
        /*mainRenderTarget.bindWrite(false);
        RenderUtils.bloomBlit(bloomFinalTarget, width, height, 1.0f);
        bloomFinalTarget.bindWrite(false);*/
        mainRenderTarget.bindWrite(false);
    }

    public void postBloom(float deltaTracker) {
        if (!this.shouldApplyBloom) {
            return;
        }

        this.finalTarget.bindWrite(false);
        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget mainRenderTarget = minecraft.getMainRenderTarget();

        this.effect.process(deltaTracker);
        mainRenderTarget.bindWrite(false);
        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();

        RenderUtils.bloomBlit(this.surroundTarget, width, height, 1.2f);
        RenderUtils.bloomBlit(this.finalTarget, width, height, 1.8f);
        this.shouldApplyBloom = false;

    }
    public void setApplyBloom() {
        shouldApplyBloom = true;
    }

    public void setup(ResourceProvider provider) {
        Minecraft minecraft = Minecraft.getInstance();
        TextureManager textureManager = minecraft.getTextureManager();

        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();

        if (this.effect != null) {
            this.effect.close();
        }

        try {
            this.effect = new PostChain(
                    textureManager, provider,
                    minecraft.getMainRenderTarget(),
                    EFFECT_LOCATION
            );
            this.effect.resize(width, height);
            this.finalTarget = this.effect.getTempTarget("final");
            this.surroundTarget = this.effect.getTempTarget("surround");
        }catch (IOException ioexception) {
            ThatSkyInteractions.LOGGER.warn("Failed to load shader: {}", EFFECT_LOCATION, ioexception);
        } catch (JsonSyntaxException jsonsyntaxexception) {
            ThatSkyInteractions.LOGGER.warn("Failed to parse shader: {}", EFFECT_LOCATION, jsonsyntaxexception);
        }
    }

    public void resize(int width, int height) {
        if (this.effect != null) {
            this.effect.resize(width, height);
        }
    }

    public RenderTarget getFinalTarget() {
        return this.finalTarget;
    }
}
