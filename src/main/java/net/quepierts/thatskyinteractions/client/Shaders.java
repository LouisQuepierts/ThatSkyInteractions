package net.quepierts.thatskyinteractions.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Shaders {
    @Nullable
    private static ShaderInstance roundRect;

    @Nullable
    private static ShaderInstance ring;

    @Nullable
    private static ShaderInstance glowingRing;

    @Nullable
    private static ShaderInstance crossLightSpot;

    @Nullable
    private static ShaderInstance doubleCrossLightSpot;

    @Nullable
    private static ShaderInstance halo;
    
    public static ShaderInstance getRoundRectShader() {
        return Objects.requireNonNull(roundRect, "Attempted to call getRoundRectShader before shaders have finished loading.");
    }

    public static ShaderInstance getRingShader() {
        return Objects.requireNonNull(ring, "Attempted to call getRingShader before shaders have finished loading.");
    }

    public static ShaderInstance getGlowingRingShader() {
        return Objects.requireNonNull(glowingRing, "Attempted to call getGlowingRingShader before shaders have finished loading.");
    }

    public static ShaderInstance getCrossLightSpotShader() {
        return Objects.requireNonNull(crossLightSpot, "Attempted to call getCrossLightSpotShader before shaders have finished loading.");
    }

    public static ShaderInstance getDoubleCrossLightSpotShader() {
        return Objects.requireNonNull(doubleCrossLightSpot, "Attempted to call getDoubleCrossLightSpotShader before shaders have finished loading.");
    }

    public static ShaderInstance getHalo() {
        return Objects.requireNonNull(halo, "Attempted to call getHaloShader before shaders have finished loading.");
    }

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(
                new ShaderInstance(
                        event.getResourceProvider(),
                        ThatSkyInteractions.getLocation("round_rect"),
                        DefaultVertexFormat.POSITION_TEX_COLOR
                ),
                (shader) -> roundRect = shader
        );

        event.registerShader(
                new ShaderInstance(
                        event.getResourceProvider(),
                        ThatSkyInteractions.getLocation("ring"),
                        DefaultVertexFormat.POSITION_TEX_COLOR
                ),
                (shader) -> ring = shader
        );

        event.registerShader(
                new ShaderInstance(
                        event.getResourceProvider(),
                        ThatSkyInteractions.getLocation("ring_glow"),
                        DefaultVertexFormat.POSITION_TEX_COLOR
                ),
                (shader) -> glowingRing = shader
        );

        event.registerShader(

                new ShaderInstance(
                        event.getResourceProvider(),
                        ThatSkyInteractions.getLocation("cross_light_spot"),
                        DefaultVertexFormat.POSITION_TEX_COLOR
                ),
                (shader) -> crossLightSpot = shader
        );

        event.registerShader(

                new ShaderInstance(
                        event.getResourceProvider(),
                        ThatSkyInteractions.getLocation("double_cross_light_spot"),
                        DefaultVertexFormat.POSITION_TEX_COLOR
                ),
                (shader) -> doubleCrossLightSpot = shader
        );

        event.registerShader(
                new ShaderInstance(
                        event.getResourceProvider(),
                        ThatSkyInteractions.getLocation("halo"),
                        DefaultVertexFormat.POSITION_TEX_COLOR
                ),
                (shader) -> halo = shader
        );
    }
}
