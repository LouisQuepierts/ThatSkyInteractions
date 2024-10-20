package net.quepierts.thatskyinteractions.client.render.bloom;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.registry.Shaders;
import net.quepierts.thatskyinteractions.client.render.pipeline.BatchRenderer;
import net.quepierts.thatskyinteractions.client.render.pipeline.IRenderAction;
import net.quepierts.thatskyinteractions.client.render.pipeline.RenderPrepareData;
import net.quepierts.thatskyinteractions.client.render.pipeline.VertexBufferManager;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;
import org.joml.Matrix4f;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class BloomRenderer {
    public static final ResourceLocation EFFECT_LOCATION = ThatSkyInteractions.getLocation("shaders/post/bloom.json");

    private final BatchRenderer batchRenderer;
    private final Map<BlockEntity, List<RenderEntry>> renderActions;
    private PostChain effect;
    private RenderTarget finalTarget;
    private RenderTarget surroundTarget;

    private boolean shouldApplyBloom = false;

    public BloomRenderer(VertexBufferManager vertexBufferManager) {
        this.batchRenderer = new BatchRenderer(vertexBufferManager);
        this.renderActions = new Object2ObjectOpenHashMap<>();
    }

    public void drawObjects(Matrix4f modelViewMatrix, Matrix4f projectionMatrix, Vec3 cameraPosition, float partialTick) {
        if (!this.shouldApplyBloom && this.renderActions.isEmpty()) {
            return;
        }

        this.renderActions.entrySet().removeIf((entry) -> {
            boolean removed = entry.getKey().isRemoved();
            if (!removed) {
                for (RenderEntry renderEntry : entry.getValue()) {
                    this.batchRenderer.toBatch(renderEntry.mesh, renderEntry.action);
                }
            }
            return removed;
        });

        this.finalTarget.clear(Minecraft.ON_OSX);
        this.surroundTarget.clear(Minecraft.ON_OSX);

        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget mainRenderTarget = minecraft.getMainRenderTarget();
        mainRenderTarget.bindWrite(false);
        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();
        RenderUtils.blitDepth(mainRenderTarget, this.finalTarget, width, height);

        this.finalTarget.bindWrite(false);


        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();

        Matrix4f view = new Matrix4f(modelViewMatrix).translate(
                (float) -cameraPosition.x,
                (float) -cameraPosition.y,
                (float) -cameraPosition.z
        );
        this.batchRenderer.endBatch(projectionMatrix, view);

        RenderSystem.enableCull();
        RenderSystem.disableDepthTest();

        VertexBuffer.unbind();


        this.effect.process(partialTick);
        mainRenderTarget.bindWrite(false);

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

    public void addRenderAction(
            final BlockEntity blockEntity,
            final ModelResourceLocation meshLocation,
            final Matrix4f transformation,
            final ResourceLocation textureLocation
    ) {
        List<RenderEntry> list = this.renderActions.computeIfAbsent(blockEntity, (b) -> new ObjectArrayList<>());
        IRenderAction action = new RenderPrepareData(
                Shaders.Batch.getGlowShader(),
                new Matrix4f(transformation),
                textureLocation
        );
        list.add(new RenderEntry(meshLocation, action));
    }

    public void removeRenderAction(final BlockEntity blockEntity) {
        this.renderActions.remove(blockEntity);
    }

    public void clearRenderAction(final BlockEntity blockEntity) {
        List<RenderEntry> list = this.renderActions.get(blockEntity);
        if (list != null) {
            list.clear();
        }
    }

    public void cleanup() {
        this.batchRenderer.cleanup();
        this.renderActions.clear();
    }

    private record RenderEntry(
            ModelResourceLocation mesh,
            IRenderAction action
    ) {}
}
