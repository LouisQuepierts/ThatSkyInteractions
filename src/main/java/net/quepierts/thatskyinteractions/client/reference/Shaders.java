package net.quepierts.thatskyinteractions.client.reference;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.render.cloud.CloudRenderer;
import net.quepierts.thatskyinteractions.client.render.pipeline.BatchShaderInstance;
import net.quepierts.thatskyinteractions.client.render.pipeline.BloomRenderDispatch;
import net.quepierts.thatskyinteractions.client.shader.SDFGraphicsShaderInstance;
import net.quepierts.thatskyinteractions.client.shader.ShaderHolder;
import net.quepierts.thatskyinteractions.client.shader.ShaderList;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Shaders {

    public static final ShaderHolder<SDFGraphicsShaderInstance> GRAPHICS;

    public static final ShaderHolder<ShaderInstance>            LIGHT_SPOT;
    public static final ShaderHolder<ShaderInstance>            CROSS_LIGHT_SPOT;
    public static final ShaderHolder<ShaderInstance>            DOUBLE_CROSS_LIGHT_SPOT;
    public static final ShaderHolder<ShaderInstance>            HALO;
    public static final ShaderHolder<ShaderInstance>            BLOOM_BLIT;
    public static final ShaderHolder<ShaderInstance>            CLOUDS;

    public static final ShaderHolder<ShaderInstance>            BLOOM_DOWNSAMPLE;
    public static final ShaderHolder<ShaderInstance>            BLOOM_UPSAMPLE;
    public static final ShaderHolder<ShaderInstance>            BLOOM_POST;

    private static final ShaderList SHADERS;

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        SHADERS.onRegisterShader(event);
        Batch.onRegisterShaders(event);

        var provider = event.getResourceProvider();
        //PostEffects.setup(provider);
        CloudRenderer.INSTANCE.setup(provider);
        BloomRenderDispatch.INSTANCE.setup(provider);
    }

    public static void resize(int width, int height) {
        //PostEffects.resize(width, height);
        ThatSkyInteractions instance = ThatSkyInteractions.getInstance();

        if (instance == null) {
            return;
        }

        CloudRenderer.INSTANCE.resize(width, height);
        BloomRenderDispatch.INSTANCE.resize(width, height);
    }

    public static final class Batch {
        private static BatchShaderInstance lighted;

        private static BatchShaderInstance glow;

        @NotNull
        public static BatchShaderInstance getLightedShader() {
            return Objects.requireNonNull(lighted, "Attempted to call getLightedShader before shaders have finished loading.");
        }

        @NotNull
        public static BatchShaderInstance getGlowShader() {
            return Objects.requireNonNull(glow, "Attempted to call getGlowShader before shaders have finished loading.");
        }

        private static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
            ResourceProvider provider = event.getResourceProvider();
            event.registerShader(
                    new BatchShaderInstance(
                            provider,
                            ThatSkyInteractions.getLocation("batch/lighted"),
                            DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL
                    ),
                    (shader) -> lighted = (BatchShaderInstance) shader
            );

            event.registerShader(
                    new BatchShaderInstance(
                            provider,
                            ThatSkyInteractions.getLocation("batch/glow"),
                            DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL
                    ),
                    (shader) -> glow = (BatchShaderInstance) shader
            );
        }
    }

    static {
        SHADERS             = new ShaderList(ThatSkyInteractions.MODID);

        GRAPHICS            = SHADERS.register("sdf_graphics", DefaultVertexFormat.POSITION_TEX_COLOR, SDFGraphicsShaderInstance::new);

        LIGHT_SPOT          = SHADERS.register("light_spot", DefaultVertexFormat.POSITION_TEX_COLOR);
        HALO                = SHADERS.register("halo", DefaultVertexFormat.POSITION_TEX_COLOR);
        BLOOM_BLIT          = SHADERS.register("bloom_blit", DefaultVertexFormat.POSITION_TEX_COLOR);
        CLOUDS              = SHADERS.register("clouds", DefaultVertexFormat.POSITION_COLOR_NORMAL);

        BLOOM_DOWNSAMPLE    = SHADERS.register("bloom/down_sample", DefaultVertexFormat.BLIT_SCREEN);
        BLOOM_UPSAMPLE      = SHADERS.register("bloom/up_sample", DefaultVertexFormat.BLIT_SCREEN);
        BLOOM_POST          = SHADERS.register("bloom/post", DefaultVertexFormat.BLIT_SCREEN);

        CROSS_LIGHT_SPOT    = SHADERS.register("cross_light_spot", DefaultVertexFormat.POSITION_TEX_COLOR);
        DOUBLE_CROSS_LIGHT_SPOT = SHADERS.register("double_cross_light_spot", DefaultVertexFormat.POSITION_TEX_COLOR);
    }
}
