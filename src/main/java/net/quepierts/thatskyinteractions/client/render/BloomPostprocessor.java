package net.quepierts.thatskyinteractions.client.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.reference.Shaders;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class BloomPostprocessor implements AutoCloseable {
    private static final ResourceLocation NOISE = ThatSkyInteractions.getLocation("textures/noise/output_128x128_tri.png");
    private static final int DOWN_STEP = 1;
    private final RenderTarget[] downsampleTargets;
    private final RenderTarget[] upsampleTargets;

    private final int steps;

    public BloomPostprocessor(int steps) {
        this.downsampleTargets = new RenderTarget[steps];
        this.upsampleTargets = new RenderTarget[steps - 1];

        this.steps = steps;

        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();

        int width = window.getWidth();
        int height = window.getHeight();

        for (int i = 0; i < steps; i++) {
            width >>= DOWN_STEP;
            height >>= DOWN_STEP;
            this.downsampleTargets[i] = new TextureTarget(width, height, false, Minecraft.ON_OSX);
            this.downsampleTargets[i].setFilterMode(GL11.GL_LINEAR);
            this.downsampleTargets[i].setClearColor(0, 0, 0, 1);

            if (i < steps - 1) {
                this.upsampleTargets[i] = new TextureTarget(width, height, false, Minecraft.ON_OSX);
                this.upsampleTargets[i].setFilterMode(GL11.GL_LINEAR);
                this.upsampleTargets[i].setClearColor(0, 0, 0, 1);
            }
        }
    }

    public void resize(int width, int height) {
        for (int i = 0; i < this.steps; i++) {
            width >>= DOWN_STEP;
            height >>= DOWN_STEP;

            this.downsampleTargets[i].resize(width, height, true);
            this.downsampleTargets[i].setFilterMode(GL11.GL_LINEAR);

            if (i < steps - 1) {
                this.upsampleTargets[i].resize(width, height, true);
                this.upsampleTargets[i].setFilterMode(GL11.GL_LINEAR);
            }
        }
    }

    @Override
    public void close() {
        for (RenderTarget target : this.downsampleTargets) {
            target.destroyBuffers();
        }

        for (RenderTarget target : this.upsampleTargets) {
            target.destroyBuffers();
        }
    }

    public void process(final RenderTarget input) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget main = minecraft.getMainRenderTarget();
        AbstractTexture noise = minecraft.getTextureManager().getTexture(NOISE);

        this.downSample(input, noise);
        this.upSample(noise);

        main.bindWrite(true);
        RenderSystem.disableBlend();

        ShaderInstance post = Shaders.BLOOM_POST.getInstance();

        post.setSampler("DiffuseSampler", this.upsampleTargets[0]);
        post.setSampler("ScreenSampler", main);
        post.setSampler("BaseSampler", input);
        post.apply();

        quad();

        post.clear();
    }

    private void downSample(RenderTarget input, AbstractTexture noise) {
        ShaderInstance downsample = Shaders.BLOOM_DOWNSAMPLE.getInstance();

        this.downsampleTargets[0].bindWrite(true);
        downsample.apply();

        int sampler0 = Uniform.glGetUniformLocation(downsample.getId(), "CurrentSampler");
        int sampler1 = Uniform.glGetUniformLocation(downsample.getId(), "NoiseSampler");

        GlStateManager._activeTexture(GL13.GL_TEXTURE1);
        noise.bind();

        Uniform resolution = downsample.getUniform("Resolution");
        Uniform index = downsample.getUniform("FrameIndex");
        assert resolution != null;
        assert index != null;

        Uniform.uploadInteger(sampler0, 0);
        Uniform.uploadInteger(sampler1, 1);
        resolution.set(1.0F / input.width, 1.0F / input.height);
        resolution.upload();

        index.set(0);
        index.upload();

        this.downsampleTargets[0].clear(false);
        blit(input, this.downsampleTargets[0], resolution);

        for (int i = 1; i < this.steps; i++) {
            RenderTarget src = this.downsampleTargets[i - 1];
            RenderTarget dst = this.downsampleTargets[i];
            dst.clear(false);

            src.bindRead();
            GlStateManager._activeTexture(GL13.GL_TEXTURE0);
            src.bindRead();
            index.set(i);
            index.upload();

            blit(src, dst, resolution);
        }

        downsample.clear();
    }

    private void upSample(AbstractTexture noise) {
        ShaderInstance upsample = Shaders.BLOOM_UPSAMPLE.getInstance();
        upsample.apply();

        int sampler0 = Uniform.glGetUniformLocation(upsample.getId(), "CurrentSampler");
        int sampler1 = Uniform.glGetUniformLocation(upsample.getId(), "PreviousSampler");
        int sampler2 = Uniform.glGetUniformLocation(upsample.getId(), "NoiseSampler");

        GlStateManager._activeTexture(GL13.GL_TEXTURE2);
        noise.bind();

        Uniform.uploadInteger(sampler0, 0);
        Uniform.uploadInteger(sampler1, 1);
        Uniform.uploadInteger(sampler2, 2);
        Uniform resolution = upsample.getUniform("Resolution");
        Uniform index = upsample.getUniform("FrameIndex");
        assert resolution != null;
        assert index != null;

        GlStateManager._activeTexture(GL13.GL_TEXTURE1);
        this.downsampleTargets[this.steps - 1].bindRead();

        index.set(this.steps - 1);
        index.upload();
        this.upsampleTargets[this.steps - 2].clear(false);
        blit(this.downsampleTargets[this.steps - 2], this.upsampleTargets[this.steps - 2], resolution);

        for (int i = this.steps - 2; i > 0; i--) {
            GlStateManager._activeTexture(GL13.GL_TEXTURE1);
            this.upsampleTargets[i].bindRead();

            RenderTarget src = this.downsampleTargets[i - 1];
            RenderTarget dst = this.upsampleTargets[i - 1];
            dst.clear(false);

            index.set(i);
            index.upload();

            blit(src, dst, resolution);
        }


        /*GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.downsampleTargets[0].getColorTextureId());

        resolution.set(1.0F / output.width, 1.0F / output.height);
        resolution.upload();
        index.set(0);
        index.upload();

        blit(this.upsampleTargets[0], output);*/
    }

    private static void blit(RenderTarget src, RenderTarget dst, Uniform resolution) {
        GlStateManager._activeTexture(GL13.GL_TEXTURE0);
        src.bindRead();
        dst.bindWrite(true);

        resolution.set(1.0f / src.width, 1.0f / src.height);
        resolution.upload();

        quad();
    }

    private static void quad() {
        BufferBuilder bufferbuilder = RenderSystem.renderThreadTesselator().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLIT_SCREEN);
        bufferbuilder.addVertex(0.0F, 0.0F, 0.0F);
        bufferbuilder.addVertex(1.0F, 0.0F, 0.0F);
        bufferbuilder.addVertex(1.0F, 1.0F, 0.0F);
        bufferbuilder.addVertex(0.0F, 1.0F, 0.0F);
        BufferUploader.draw(bufferbuilder.buildOrThrow());
    }
}
