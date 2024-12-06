package net.quepierts.thatskyinteractions.client.render.pipeline;

import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.Objects;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public final class RenderPrepareData implements IRenderAction {
    private final @NotNull Supplier<BatchShaderInstance> shader;
    private final @NotNull Matrix4f transformationMatrix;
    private final @NotNull ResourceLocation texture;
    private final int color;

    public RenderPrepareData(
            @NotNull Supplier<BatchShaderInstance> shader,
            @NotNull Matrix4f transformationMatrix,
            @NotNull ResourceLocation texture,
            int color
    ) {
        this.shader = shader;
        this.transformationMatrix = transformationMatrix;
        this.texture = texture;
        this.color = color;
    }

    @Override
    public void apply(
            final VertexBuffer buffer,
            final Matrix4f projectionMatrix,
            final Matrix4f frustumMatrix
    ) {
        BatchShaderInstance instance = this.shader.get();

        if (instance == null) {
            return;
        }

        if (instance.TRANSFORMATION_MATRIX != null) {
            instance.TRANSFORMATION_MATRIX.set(this.transformationMatrix);
            instance.TRANSFORMATION_MATRIX.upload();
        }

        instance.refreshColor(this.color);
        instance.refreshTexture(this.texture);

        buffer.draw();
    }

    @Override
    public BatchShaderInstance shader() {
        return shader.get();
    }

    public @NotNull Matrix4f transformationMatrix() {
        return transformationMatrix;
    }

    public @NotNull ResourceLocation texture() {
        return texture;
    }

    public int color() {
        return color;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (RenderPrepareData) obj;
        return Objects.equals(this.shader, that.shader) &&
                Objects.equals(this.transformationMatrix, that.transformationMatrix) &&
                Objects.equals(this.texture, that.texture) &&
                this.color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(shader, transformationMatrix, texture, color);
    }

    @Override
    public String toString() {
        return "RenderPrepareData[" +
                "shader=" + shader + ", " +
                "transformationMatrix=" + transformationMatrix + ", " +
                "texture=" + texture + ", " +
                "color=" + color + ']';
    }

}
