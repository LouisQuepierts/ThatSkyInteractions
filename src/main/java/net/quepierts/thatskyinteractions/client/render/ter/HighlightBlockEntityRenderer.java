package net.quepierts.thatskyinteractions.client.render.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.registry.RenderTypes;
import net.quepierts.thatskyinteractions.client.render.bloom.BloomRenderer;
import net.quepierts.thatskyinteractions.client.render.pipeline.VertexBufferManager;

@OnlyIn(Dist.CLIENT)
public abstract class HighlightBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
    protected static final ModelPart.Cube CUBE;

    protected final BloomRenderer bloomRenderer;

    protected HighlightBlockEntityRenderer() {
        this.bloomRenderer = ThatSkyInteractions.getInstance().getClient().getBloomRenderer();
    }

    protected void renderHighLight(PoseStack poseStack, int color, int combinedLight, int combinedOverlay) {
//        VertexConsumer vertexConsumer = RenderTypes.getBufferSource().getBuffer(RenderTypes.BLOOM.apply(RenderTypes.TEXTURE, false));
//        CUBE.compile(poseStack.last(), vertexConsumer, combinedLight, combinedOverlay, color);

        poseStack.pushPose();
        this.bloomRenderer.batchRender(VertexBufferManager.CUBE, poseStack.last().pose(), RenderTypes.TEXTURE);
        poseStack.popPose();
    }

    static {
        CubeListBuilder builder = CubeListBuilder.create().addBox(4, 4, 4, 8, 8, 8);
        CUBE = builder.getCubes().getFirst().bake(64, 64);
    }
}
