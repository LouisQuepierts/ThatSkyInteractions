package net.quepierts.thatskyinteractions.client.render.pipeline;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class VboRenderDispatch extends RenderDispatch<BlockEntity> {
    public static final VboRenderDispatch INSTANCE = new VboRenderDispatch(VertexBufferManager.INSTANCE);
    VboRenderDispatch(VertexBufferManager vertexBufferManager) {
        super(vertexBufferManager);
    }

    @Override
    protected boolean shouldRemove(BlockEntity key) {
        return key.isRemoved();
    }

    @Override
    protected void prepareRender(float partialTick) {
        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);

        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
    }

    @Override
    protected void afterRender(float partialTick) {
        RenderSystem.enableCull();
        RenderSystem.disableDepthTest();
    }
}
