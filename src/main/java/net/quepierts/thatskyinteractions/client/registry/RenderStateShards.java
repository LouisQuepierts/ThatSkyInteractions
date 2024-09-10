package net.quepierts.thatskyinteractions.client.registry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;

@OnlyIn(Dist.CLIENT)
public class RenderStateShards {
    public static final RenderStateShard.ShaderStateShard CLOUD = new RenderStateShard.ShaderStateShard(Shaders::getCloudShader);

    public static final RenderStateShard.OutputStateShard BLOOM_TARGET = new RenderStateShard.OutputStateShard(
            "bloom_target",
            () -> {
                if (Minecraft.useShaderTransparency()) {
                    ThatSkyInteractions.getInstance().getClient().getBloomRenderer().getFinalTarget().bindWrite(false);
                }
            },
            () -> {
                if (Minecraft.useShaderTransparency()) {
                    Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
                }
            }
    );
}
