package net.quepierts.thatskyinteractions.client.render.pipeline;

import com.mojang.blaze3d.vertex.VertexBuffer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public interface IRenderAction {
    void apply(final VertexBuffer buffer, final Matrix4f projectionMatrix, final Matrix4f frustumMatrix);

    @NotNull
    BatchShaderInstance shader();
}
