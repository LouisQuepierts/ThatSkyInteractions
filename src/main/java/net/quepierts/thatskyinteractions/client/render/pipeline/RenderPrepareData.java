package net.quepierts.thatskyinteractions.client.render.pipeline;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public record RenderPrepareData(
        @NotNull ShaderInstance shader,
        @NotNull Matrix4f transformationMatrix,
        @NotNull ResourceLocation texture
) implements IRenderAction {
    @Override
    public void apply(
            final VertexBuffer buffer,
            final Matrix4f projectionMatrix,
            final Matrix4f frustumMatrix
    ) {
        RenderSystem.setShaderTexture(0, this.texture);

        buffer.drawWithShader(
                new Matrix4f(frustumMatrix).mul(this.transformationMatrix),
                projectionMatrix,
                this.shader
        );
    }
}
