package net.quepierts.thatskyinteractions.client.registry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;

public class RenderStateShards {
    public static final RenderStateShard.ShaderStateShard CLOUD = new RenderStateShard.ShaderStateShard(Shaders::getCloudShader);

    public static final RenderStateShard.OutputStateShard BLOOM_TARGET = new RenderStateShard.OutputStateShard(
            "bloom_target",
            () -> {
                if (Minecraft.useShaderTransparency()) {
                    PostEffects.getBloomFinalTarget().bindWrite(true);
                }
            },
            () -> {
                if (Minecraft.useShaderTransparency()) {
                    Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
                }
            }
    );
}
