package net.quepierts.thatskyinteractions.client.reference;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Shaders {

    public static final ShaderHolder ROUND_RECT;

    public static final ShaderHolder SECTOR;
    public static final ShaderHolder SECTOR_STROKE;

    public static final ShaderHolder RING;
    public static final ShaderHolder CIRCLE;
    public static final ShaderHolder GLOWING_RING;
    public static final ShaderHolder LIGHT_SPOT;
    public static final ShaderHolder CROSS_LIGHT_SPOT;
    public static final ShaderHolder DOUBLE_CROSS_LIGHT_SPOT;
    public static final ShaderHolder HALO;
    public static final ShaderHolder BLOOM_BLIT;
    public static final ShaderHolder CLOUDS;

    public static final ShaderHolder BLOOM_DOWNSAMPLE;
    public static final ShaderHolder BLOOM_UPSAMPLE;
    public static final ShaderHolder BLOOM_POST;

    private static final List<ShaderHolder> HOLDERS;

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        ResourceProvider provider = event.getResourceProvider();

        for (ShaderHolder holder : HOLDERS) {
            event.registerShader(
                    new ShaderInstance(
                            provider,
                            holder.getLocation(),
                            holder.getFormat()
                    ),
                    holder::setInstance
            );
        }

        Batch.onRegisterShaders(event);

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

    @Setter
    @RequiredArgsConstructor
    public static class ShaderHolder {
        @Getter
        private final ResourceLocation location;
        @Getter private final VertexFormat format;

        private ShaderInstance instance;

        public @NotNull ShaderInstance getInstance() {
            return Objects.requireNonNull(instance, "Attempted to call get shader [" + location + "] before shaders have finished loading.");
        }

        public @NotNull ShaderInstance use() {
            RenderSystem.setShader(this::getInstance);
            return this.getInstance();
        }
    }

    private static ShaderHolder register(ResourceLocation location, VertexFormat format) {
        ShaderHolder holder = new ShaderHolder(location, format);
        HOLDERS.add(holder);
        return holder;
    }

    static {
        HOLDERS = new ArrayList<>();
        ROUND_RECT = register(ThatSkyInteractions.getLocation("round_rect"), DefaultVertexFormat.POSITION_TEX_COLOR);

        SECTOR = register(ThatSkyInteractions.getLocation("sector"), DefaultVertexFormat.POSITION_TEX_COLOR);
        SECTOR_STROKE = register(ThatSkyInteractions.getLocation("sector_stroke"), DefaultVertexFormat.POSITION_TEX_COLOR);

        CIRCLE = register(ThatSkyInteractions.getLocation("circle"), DefaultVertexFormat.POSITION_TEX_COLOR);
        RING = register(ThatSkyInteractions.getLocation("ring"), DefaultVertexFormat.POSITION_TEX_COLOR);
        GLOWING_RING = register(ThatSkyInteractions.getLocation("ring_glow"), DefaultVertexFormat.POSITION_TEX_COLOR);
        LIGHT_SPOT = register(ThatSkyInteractions.getLocation("light_spot"), DefaultVertexFormat.POSITION_TEX_COLOR);
        CROSS_LIGHT_SPOT = register(ThatSkyInteractions.getLocation("cross_light_spot"), DefaultVertexFormat.POSITION_TEX_COLOR);
        DOUBLE_CROSS_LIGHT_SPOT = register(ThatSkyInteractions.getLocation("double_cross_light_spot"), DefaultVertexFormat.POSITION_TEX_COLOR);
        HALO = register(ThatSkyInteractions.getLocation("halo"), DefaultVertexFormat.POSITION_TEX_COLOR);
        BLOOM_BLIT = register(ThatSkyInteractions.getLocation("bloom_blit"), DefaultVertexFormat.POSITION_TEX_COLOR);
        CLOUDS = register(ThatSkyInteractions.getLocation("clouds"), DefaultVertexFormat.POSITION_COLOR_NORMAL);
        BLOOM_DOWNSAMPLE = register(ThatSkyInteractions.getLocation("bloom/down_sample"), DefaultVertexFormat.POSITION_TEX_COLOR);
        BLOOM_UPSAMPLE = register(ThatSkyInteractions.getLocation("bloom/up_sample"), DefaultVertexFormat.POSITION_TEX_COLOR);
        BLOOM_POST = register(ThatSkyInteractions.getLocation("bloom/post"), DefaultVertexFormat.POSITION_TEX_COLOR);
    }
}
