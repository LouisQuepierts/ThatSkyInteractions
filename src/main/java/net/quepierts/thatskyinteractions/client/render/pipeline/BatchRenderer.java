package net.quepierts.thatskyinteractions.client.render.pipeline;

import com.mojang.blaze3d.vertex.VertexBuffer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.client.registry.Shaders;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class BatchRenderer {
    private final VertexBufferManager vertexBufferManager;
    private final Map<ResourceLocation, List<IRenderAction>> batches;

    public BatchRenderer(VertexBufferManager vertexBufferManager) {
        this.vertexBufferManager = vertexBufferManager;
        this.batches = new Object2ObjectOpenHashMap<>();
    }

    public void toBatch(ResourceLocation meshLocation, IRenderAction action) {
        List<IRenderAction> batch = this.batches.computeIfAbsent(meshLocation, (u) -> new ObjectArrayList<>());
        batch.add(action);
    }

    public void endBatch(
            final Matrix4f projectionMatrix,
            final Matrix4f frustumMatrix
    ) {
        for (ResourceLocation location : this.batches.keySet()) {
            this.endBatch(location, projectionMatrix, frustumMatrix);
        }
    }

    public void endBatch(
            final ResourceLocation meshLocation,
            final Matrix4f projectionMatrix,
            final Matrix4f frustumMatrix
    ) {
        final List<IRenderAction> batch = this.batches.get(meshLocation);

        if (batch == null || batch.isEmpty()) {
            return;
        }

        final VertexBuffer vertexBuffer = this.vertexBufferManager.get(meshLocation);

        if (vertexBuffer == null) {
            batch.clear();
            return;
        }

        vertexBuffer.bind();
        for (IRenderAction action : batch) {
            action.apply(vertexBuffer, projectionMatrix, frustumMatrix);
        }
        VertexBuffer.unbind();

        batch.clear();
    }

    public void cleanup() {
        this.batches.clear();
    }
}
