package net.quepierts.thatskyinteractions.client.render.pipeline;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class BatchRenderer {
    private final VertexBufferManager vertexBufferManager;
    private final Map<ModelResourceLocation, List<IRenderAction>> batches;

    public BatchRenderer(VertexBufferManager vertexBufferManager) {
        this.vertexBufferManager = vertexBufferManager;
        this.batches = new Object2ObjectOpenHashMap<>();
    }

    public void toBatch(ModelResourceLocation meshLocation, IRenderAction action) {
        List<IRenderAction> batch = this.batches.computeIfAbsent(meshLocation, (u) -> new ObjectArrayList<>());
        batch.add(action);
    }

    public void endBatch(
            final Matrix4f projectionMatrix,
            final Matrix4f frustumMatrix
    ) {
        for (ModelResourceLocation location : this.batches.keySet()) {
            this.endBatch(location, projectionMatrix, frustumMatrix);
        }
    }

    public void endBatch(
            final ModelResourceLocation meshLocation,
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

        BatchShaderInstance using = null;
        for (IRenderAction action : batch) {
            if (action.shader() != using) {
                if (using != null) {
                    using.clear();
                }

                using = action.shader();

                if (using.MODEL_VIEW_MATRIX != null) {
                    using.MODEL_VIEW_MATRIX.set(frustumMatrix);
                }

                if (using.PROJECTION_MATRIX != null) {
                    using.PROJECTION_MATRIX.set(projectionMatrix);
                }

                if (using.COLOR_MODULATOR != null) {
                    using.COLOR_MODULATOR.set(RenderSystem.getShaderColor());
                }

                using.apply();
            }

            action.apply(vertexBuffer, projectionMatrix, frustumMatrix);
        }

        if (using != null) {
            using.clear();
        }
        VertexBuffer.unbind();

        batch.clear();
    }

    public void cleanup() {
        this.batches.clear();
    }
}
