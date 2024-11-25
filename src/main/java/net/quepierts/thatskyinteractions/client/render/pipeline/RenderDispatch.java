package net.quepierts.thatskyinteractions.client.render.pipeline;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.quepierts.thatskyinteractions.client.reference.Shaders;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Map;

public abstract class RenderDispatch<T> {
    private final BatchRenderer batchRenderer;
    private final Object2ObjectMap<T, List<RenderEntry>> renderActions;
    private final ObjectList<T> remove;

    private boolean render = false;

    protected RenderDispatch(VertexBufferManager vertexBufferManager) {
        batchRenderer = new BatchRenderer(vertexBufferManager);

        renderActions = new Object2ObjectOpenHashMap<>();
        remove = new ObjectArrayList<>();
    }

    protected boolean shouldRender() {
        return this.render;
    }

    protected abstract boolean shouldRemove(T key);

    protected void prepareRender(float partialTick) {}

    protected void afterRender(float partialTick) {}

    public void batchRender(
            final ModelResourceLocation meshLocation,
            final Matrix4f transformation,
            final ResourceLocation textureLocation,
            final int color
    ) {
        this.batchRenderer.toBatch(meshLocation, new RenderPrepareData(
                Shaders.Batch.getGlowShader(),
                new Matrix4f(transformation),
                textureLocation,
                color
        ));

        this.render = true;
    }

    public void addRenderAction(
            final T t,
            final ModelResourceLocation meshLocation,
            final Matrix4f transformation,
            final ResourceLocation textureLocation,
            final int color
    ) {
        List<RenderEntry> list = this.renderActions.computeIfAbsent(t, (b) -> new ObjectArrayList<>());
        IRenderAction action = new RenderPrepareData(
                Shaders.Batch.getGlowShader(),
                new Matrix4f(transformation),
                textureLocation,
                color
        );
        list.add(new RenderEntry(meshLocation, action));
        this.render = true;
    }

    public void removeRenderAction(final T t) {
        this.renderActions.remove(t);
    }

    public void clearRenderAction(final T t) {
        List<RenderEntry> list = this.renderActions.get(t);
        if (list != null) {
            list.clear();
        }
    }

    public void cleanup() {
        this.batchRenderer.cleanup();
        this.renderActions.clear();
    }

    public void drawObjects(Matrix4f modelViewMatrix, Matrix4f projectionMatrix, Vec3 cameraPosition, float partialTick) {
        if (!this.shouldRender() && this.renderActions.isEmpty()) {
            return;
        }

        for (Map.Entry<T, List<RenderEntry>> mapEntry : this.renderActions.entrySet()) {
            boolean removed = this.shouldRemove(mapEntry.getKey());

            if (removed) {
                this.remove.add(mapEntry.getKey());
            } else {
                for (RenderEntry renderEntry : mapEntry.getValue()) {
                    this.batchRenderer.toBatch(renderEntry.mesh, renderEntry.action);
                }
            }
        }

        if (!this.remove.isEmpty()) {
            for (T t : this.remove) {
                this.renderActions.remove(t);
            }
            this.remove.clear();
        }

        this.prepareRender(partialTick);

        Matrix4f view = new Matrix4f(modelViewMatrix).translate(
                (float) -cameraPosition.x,
                (float) -cameraPosition.y,
                (float) -cameraPosition.z
        );
        this.batchRenderer.endBatch(projectionMatrix, view);

        this.afterRender(partialTick);
        this.render = false;
    }

    private record RenderEntry(
            ModelResourceLocation mesh,
            IRenderAction action
    ) {}
}
