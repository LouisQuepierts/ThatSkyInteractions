package net.quepierts.thatskyinteractions.client.render.pipeline;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;

import java.io.IOException;

public class BloomRenderDispatch extends RenderDispatch<BlockEntity> {
    public static final ResourceLocation EFFECT_LOCATION = ThatSkyInteractions.getLocation("shaders/post/bloom.json");

    private PostChain effect;
    private RenderTarget finalTarget;
    private RenderTarget surroundTarget;

    private boolean shouldApplyBloom = false;

    public BloomRenderDispatch(VertexBufferManager vertexBufferManager) {
        super(vertexBufferManager);
    }

    @Override
    protected boolean shouldRemove(BlockEntity key) {
        return key.isRemoved();
    }

    public void prepareBuffer() {
        this.finalTarget.clear(Minecraft.ON_OSX);
        this.surroundTarget.clear(Minecraft.ON_OSX);

        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget mainRenderTarget = minecraft.getMainRenderTarget();
        mainRenderTarget.bindWrite(false);
        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();
        RenderUtils.blitDepth(mainRenderTarget, this.finalTarget, width, height);
        mainRenderTarget.bindWrite(false);
    }

    @Override
    protected void prepareRender(float partialTick) {
        this.finalTarget.bindWrite(false);

        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
    }

    @Override
    protected void afterRender(float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget mainRenderTarget = minecraft.getMainRenderTarget();
        mainRenderTarget.bindWrite(false);
        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();

        RenderSystem.enableCull();
        RenderSystem.disableDepthTest();

        VertexBuffer.unbind();

        this.effect.process(partialTick);
        mainRenderTarget.bindWrite(false);

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
