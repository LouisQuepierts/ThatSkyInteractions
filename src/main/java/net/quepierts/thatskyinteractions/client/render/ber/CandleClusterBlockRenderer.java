package net.quepierts.thatskyinteractions.client.render.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.block.CandleClusterBlock;
import net.quepierts.thatskyinteractions.block.entity.CandleClusterBlockEntity;
import net.quepierts.thatskyinteractions.client.render.section.StaticModelRenderer;
import net.quepierts.thatskyinteractions.registry.Blocks;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class CandleClusterBlockRenderer implements StaticBlockEntityRenderer<CandleClusterBlockEntity> {
    private final BlockRenderDispatcher dispatcher;

    public CandleClusterBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.dispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(
            @NotNull CandleClusterBlockEntity cluster,
            @NotNull StaticModelRenderer renderer,
            @NotNull BlockPos blockPos,
            @NotNull PoseStack poseStack
    ) {
        BlockState normalState = Blocks.CANDLE_CLUSTER.get().defaultBlockState();
        BlockState litState = Blocks.CANDLE_CLUSTER.get().defaultBlockState().setValue(CandleClusterBlock.LIT, true);
        BakedModel normalModel = dispatcher.getBlockModel(normalState);
        BakedModel litModel = dispatcher.getBlockModel(litState);

        ShortArrayList candles = cluster.getCandles();

        poseStack.translate(-0.5f, 0, -0.5f);
        for (Short candle : candles) {
            float x = (CandleClusterBlockEntity.getCandleX(candle) + 1) / 16.0f;
            float z = (CandleClusterBlockEntity.getCandleZ(candle) + 1) / 16.0f;
            boolean lit = CandleClusterBlockEntity.getCandleLit(candle);

            poseStack.pushPose();
            poseStack.translate(x, 0, z);

            if (lit) {
                renderer.render(
                        litModel,
                        litState,
                        blockPos,
                        poseStack
                );
            } else {
                renderer.render(
                        normalModel,
                        normalState,
                        blockPos,
                        poseStack
                );
            }

            poseStack.popPose();
        }
    }
}
