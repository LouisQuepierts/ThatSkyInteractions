package net.quepierts.thatskyinteractions.client.render.pipeline;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.io.IOException;

@OnlyIn(Dist.CLIENT)
public class TransformShader extends ShaderInstance {
    public final Uniform TRANSFORMATION_MATRIX;
    public TransformShader(ResourceProvider provider, ResourceLocation shaderLocation, VertexFormat format) throws IOException {
        super(provider, shaderLocation, format);

        this.TRANSFORMATION_MATRIX = this.getUniform("TransMat");
    }
}
