package net.quepierts.thatskyinteractions.client.render.pipeline;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.render.BloomPostprocessor;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;

import java.io.IOException;

public final class BloomRenderDispatch extends RenderDispatch<BlockEntity> {
    public static final BloomRenderDispatch INSTANCE = new BloomRenderDispatch(VertexBufferManager.INSTANCE);
    public static final ResourceLocation EFFECT_LOCATION = ThatSkyInteractions.getLocation("shaders/post/bloom.json");

    private BloomPostprocessor postprocessor;
    private RenderTarget bloomTarget;

    private boolean shouldApplyBloom = false;

    BloomRenderDispatch(VertexBufferManager vertexBufferManager) {
        super(vertexBufferManager);
    }

    @Override
    protected boolean shouldRemove(BlockEntity key) {
        return key.isRemoved();
    }

    public void prepareBuffer() {
        this.bloomTarget.clear(Minecraft.ON_OSX);

        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget mainRenderTarget = minecraft.getMainRenderTarget();
        mainRenderTarget.bindWrite(false);
        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();
        RenderUtils.blitDepth(mainRenderTarget, this.bloomTarget, width, height);
        mainRenderTarget.bindWrite(false);
    }

    @Override
    protected void prepareRender(float partialTick) {
        this.bloomTarget.bindWrite(true);

        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
    }

    @Override
    protected void afterRender(float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget mainRenderTarget = minecraft.getMainRenderTarget();
        mainRenderTarget.bindWrite(false);

        RenderSystem.enableCull();
        RenderSystem.disableDepthTest();

        VertexBuffer.unbind();

        this.postprocessor.process(this.bloomTarget);
//        this.bloomTarget.blitToScreen(width, height);
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

        if (this.postprocessor != null) {
            this.postprocessor.close();
        }

        if (this.bloomTarget != null) {
            this.bloomTarget.destroyBuffers();
        }

        this.postprocessor = new BloomPostprocessor(5);
        this.bloomTarget = new TextureTarget(width, height, true, Minecraft.ON_OSX);
        this.bloomTarget.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void resize(int width, int height) {
        if (this.postprocessor != null) {
            this.postprocessor.resize(width, height);
        }

        if (this.bloomTarget != null) {
            this.bloomTarget.unbindRead();
            this.bloomTarget.unbindWrite();
            this.bloomTarget.resize(width, height, Minecraft.ON_OSX);
        }
    }

    public RenderTarget getFinalTarget() {
        return this.bloomTarget;
    }
}
