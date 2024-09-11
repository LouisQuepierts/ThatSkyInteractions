package net.quepierts.thatskyinteractions.client.render.bloom;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.SequencedMap;

@OnlyIn(Dist.CLIENT)
public class BloomBufferSource extends MultiBufferSource.BufferSource {
    public BloomBufferSource(ByteBufferBuilder sharedBuffer, SequencedMap<RenderType, ByteBufferBuilder> fixedBuffers) {
        super(sharedBuffer, fixedBuffers);
    }

    public VertexConsumer getBuffer(RenderType renderType, RenderTarget finalTarget) {
        boolean changedBuffer = false;
        BufferBuilder bufferbuilder = this.startedBuilders.get(renderType);
        if (bufferbuilder != null && !renderType.canConsolidateConsecutiveGeometry()) {
            changedBuffer = true;
            finalTarget.bindWrite(false);
            this.endBatch(renderType, bufferbuilder);
            bufferbuilder = null;
        }

        if (bufferbuilder == null) {
            ByteBufferBuilder bytebufferbuilder = this.fixedBuffers.get(renderType);
            if (bytebufferbuilder != null) {
                bufferbuilder = new BufferBuilder(bytebufferbuilder, renderType.mode(), renderType.format());
            } else {
                if (this.lastSharedType != null) {
                    if (!changedBuffer) {
                        changedBuffer = true;
                        finalTarget.bindWrite(false);
                    }
                    this.endBatch(this.lastSharedType);
                }

                bufferbuilder = new BufferBuilder(this.sharedBuffer, renderType.mode(), renderType.format());
                this.lastSharedType = renderType;
            }

            this.startedBuilders.put(renderType, bufferbuilder);
        }

        if (changedBuffer) {
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
        }

        return bufferbuilder;
    }

    private void endBatch(RenderType renderType, BufferBuilder builder) {
        MeshData meshdata = builder.build();
        if (meshdata != null) {
            if (renderType.sortOnUpload()) {
                ByteBufferBuilder bytebufferbuilder = this.fixedBuffers.getOrDefault(renderType, this.sharedBuffer);
                meshdata.sortQuads(bytebufferbuilder, RenderSystem.getVertexSorting());
            }

            renderType.draw(meshdata);
        }

        if (renderType.equals(this.lastSharedType)) {
            this.lastSharedType = null;
        }
    }
}
