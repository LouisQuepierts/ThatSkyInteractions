package net.quepierts.thatskyinteractions.client.render.pipeline;

import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public record RenderPrepareData(
        @NotNull BatchShaderInstance shader,
        @NotNull Matrix4f transformationMatrix,
        @NotNull ResourceLocation texture
) implements IRenderAction {
    @Override
    public void apply(
            final VertexBuffer buffer,
            final Matrix4f projectionMatrix,
            final Matrix4f frustumMatrix
    ) {
        if (this.shader.TRANSFORMATION_MATRIX != null) {
            this.shader.TRANSFORMATION_MATRIX.set(this.transformationMatrix);
            this.shader.TRANSFORMATION_MATRIX.upload();
        }

        this.shader.refreshTexture(this.texture);

        buffer.draw();
    }
}
