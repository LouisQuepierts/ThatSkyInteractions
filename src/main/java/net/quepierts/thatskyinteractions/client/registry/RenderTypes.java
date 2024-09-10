package net.quepierts.thatskyinteractions.client.registry;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.render.bloom.BloomBufferSource;

import java.util.LinkedHashMap;
import java.util.SequencedMap;
import java.util.function.BiFunction;

@OnlyIn(Dist.CLIENT)
public class RenderTypes {
    public static final ResourceLocation TEXTURE;
    public static final RenderType WOL;
    public static final BiFunction<ResourceLocation, Boolean, RenderType> BLOOM;
    private static BloomBufferSource bufferSource;

    @SuppressWarnings("unused")
    public static void onRegisterRenderBuffers(final RegisterRenderBuffersEvent event) {
        ByteBufferBuilder bloomBufferBuilder = new ByteBufferBuilder(WOL.bufferSize());
//        event.registerRenderBuffer(WOL, bloomBufferBuilder);
        SequencedMap<RenderType, ByteBufferBuilder> map = new LinkedHashMap<>();
        map.put(WOL, bloomBufferBuilder);
        bufferSource = new BloomBufferSource(new ByteBufferBuilder(786432), map);
    }

    public static BloomBufferSource getBufferSource() {
        return bufferSource;
    }

    static {
        TEXTURE = ThatSkyInteractions.getLocation("textures/entity/wing_of_light.png");

        BLOOM = Util.memoize(
                (texture, composite) -> {
                    RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                            .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                            .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                            .setCullState(RenderStateShard.NO_CULL)
                            .setLightmapState(RenderStateShard.NO_LIGHTMAP)
                            .setOutputState(RenderStateShards.BLOOM_TARGET)
                            .createCompositeState(composite);
                    return RenderType.create("bloom", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 786432, false, true, rendertype$compositestate);
                }
        );

        WOL = BLOOM.apply(TEXTURE, false);
    }
}
