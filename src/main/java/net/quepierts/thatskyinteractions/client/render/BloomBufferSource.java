package net.quepierts.thatskyinteractions.client.render;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.SequencedMap;

@OnlyIn(Dist.CLIENT)
public class BloomBufferSource extends MultiBufferSource.BufferSource {
    public BloomBufferSource(ByteBufferBuilder sharedBuffer, SequencedMap<RenderType, ByteBufferBuilder> fixedBuffers) {
        super(sharedBuffer, fixedBuffers);
    }
}
