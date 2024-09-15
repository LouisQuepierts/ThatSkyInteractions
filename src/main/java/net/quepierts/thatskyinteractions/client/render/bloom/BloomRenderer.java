package net.quepierts.thatskyinteractions.client.render.bloom;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.world.phys.Vec3;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.registry.RenderTypes;
import net.quepierts.thatskyinteractions.client.registry.Shaders;
import net.quepierts.thatskyinteractions.client.render.pipeline.BatchRenderer;
import net.quepierts.thatskyinteractions.client.render.pipeline.RenderPrepareData;
import net.quepierts.thatskyinteractions.client.render.pipeline.VertexBufferManager;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;
import org.joml.Matrix4f;

import java.io.IOException;

public class BloomRenderer {
    public static final ResourceLocation EFFECT_LOCATION = ThatSkyInteractions.getLocation("shaders/post/bloom.json");

    private final VertexBufferManager vertexBufferManager;
    private final BatchRenderer batchRenderer;
    private PostChain effect;
    private RenderTarget finalTarget;
    private RenderTarget surroundTarget;

    private boolean shouldApplyBloom = false;

    public BloomRenderer(VertexBufferManager vertexBufferManager) {
        this.vertexBufferManager = vertexBufferManager;
        this.batchRenderer = new BatchRenderer(vertexBufferManager);
    }

    public void drawObjects(PoseStack poseStack, Matrix4f frustumMatrix, Matrix4f projectionMatrix, Vec3 cameraPosition) {
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

        poseStack.pushPose();
        poseStack.mulPose(frustumMatrix);

        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();

        this.finalTarget.bindWrite(false);
        this.batchRenderer.endBatch(projectionMatrix, poseStack.last().pose());

        RenderSystem.enableCull();
        RenderSystem.disableDepthTest();

        poseStack.popPose();

        VertexBuffer.unbind();
        mainRenderTarget.bindWrite(false);
    }

    public void processBloom(float deltaTracker, final PoseStack poseStack, final Matrix4f projectionMatrix, final Matrix4f frustumMatrix, Vec3 position) {
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

//        this.finalTarget.blitToScreen(width, height);
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

    public void batchRender(
            final ModelResourceLocation meshLocation,
            final Matrix4f transformation,
            final ResourceLocation textureLocation
    ) {
        this.batchRenderer.toBatch(meshLocation, new RenderPrepareData(
                Shaders.Batch.getGlowShader(),
                new Matrix4f(transformation),
                textureLocation
        ));

        this.setApplyBloom();
    }

    public void cleanup() {
        this.batchRenderer.cleanup();
    }

    public VertexConsumer getBuffer(ResourceLocation location) {
        this.finalTarget.bindWrite(false);
        VertexConsumer buffer = RenderTypes.getBufferSource().getBuffer(RenderTypes.BLOOM.apply(location, false));
        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
        return buffer;
    }
}
