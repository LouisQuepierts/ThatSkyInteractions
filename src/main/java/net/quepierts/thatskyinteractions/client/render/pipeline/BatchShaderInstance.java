package net.quepierts.thatskyinteractions.client.render.pipeline;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@OnlyIn(Dist.CLIENT)
public class BatchShaderInstance extends ShaderInstance {
    public final Uniform TRANSFORMATION_MATRIX;
    public final int SAMPLER_0;

    private ResourceLocation lastTexture0;

    public BatchShaderInstance(ResourceProvider resourceProvider, ResourceLocation shaderLocation, VertexFormat format) throws IOException {
        super(resourceProvider, shaderLocation, format);

        this.TRANSFORMATION_MATRIX = this.getUniform("TransMat");
        this.SAMPLER_0 = Uniform.glGetUniformLocation(this.getId(), "Sampler0");
    }

    @Override
    public void apply() {
        super.apply();
        this.lastTexture0 = null;

        if (this.SAMPLER_0 != -1) {
            RenderSystem.assertOnRenderThread();
            int i = GlStateManager._getActiveTexture();
            Uniform.uploadInteger(SAMPLER_0, 0);
            RenderSystem.activeTexture(i);
            GlStateManager._activeTexture(i);
        }
    }

    public void refreshTexture(@NotNull ResourceLocation location) {
        if (location.equals(lastTexture0)) {
            return;
        }
        RenderSystem.assertOnRenderThread();
        RenderSystem.setShaderTexture(0, location);
        RenderSystem.bindTexture(RenderSystem.getShaderTexture(0));
        this.lastTexture0 = location;
    }
}
