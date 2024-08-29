package net.quepierts.thatskyinteractions.client.registry;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;

public class RenderTypes {
    public static final ResourceLocation TEXTURE;
    public static final RenderType BLOOM;
    private static ByteBufferBuilder bloomBufferBuilder;
    public static void onRegisterRenderBuffers(final RegisterRenderBuffersEvent event) {
        bloomBufferBuilder = new ByteBufferBuilder(BLOOM.bufferSize());
        event.registerRenderBuffer(BLOOM, bloomBufferBuilder);
    }

    public static ByteBufferBuilder getBloomBufferBuilder() {
        return bloomBufferBuilder;
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
    }
}
