package net.quepierts.thatskyinteractions.client.registry;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.render.BloomBufferSource;

import java.util.LinkedHashMap;
import java.util.SequencedMap;

public class RenderTypes {
    public static final ResourceLocation TEXTURE;
    public static final RenderType BLOOM;
    public static final RenderType BLOOM_RAW;
    private static BloomBufferSource bufferSource;

    public static void onRegisterRenderBuffers(final RegisterRenderBuffersEvent event) {
        ByteBufferBuilder bloomBufferBuilder = new ByteBufferBuilder(BLOOM.bufferSize());
        event.registerRenderBuffer(BLOOM, bloomBufferBuilder);
        SequencedMap<RenderType, ByteBufferBuilder> map = new LinkedHashMap<>();
        bufferSource = new BloomBufferSource(new ByteBufferBuilder(1536), map);
    }

    public static BloomBufferSource getBufferSource() {
        return bufferSource;
    }

    static {
        TEXTURE = ThatSkyInteractions.getLocation("textures/entity/wing_of_light.png");
        RenderType.CompositeState renderState = RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(TEXTURE, false, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderStateShard.NO_CULL)
                .setLightmapState(RenderStateShard.NO_LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(true);

        BLOOM = RenderType.create("wing_of_light", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, false, true, renderState);
        BLOOM_RAW = RenderType.create("wing_of_light_raw", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, false, true, renderState);
    }
}
