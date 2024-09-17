package net.quepierts.thatskyinteractions.client.render.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.block.CandleType;
import net.quepierts.thatskyinteractions.block.entity.CandleClusterBlockEntity;
import net.quepierts.thatskyinteractions.client.data.ClientTSIDataCache;
import net.quepierts.thatskyinteractions.client.gui.layer.World2ScreenWidgetLayer;
import net.quepierts.thatskyinteractions.client.render.section.StaticModelRenderer;
import net.quepierts.thatskyinteractions.client.util.CandleModels;
import net.quepierts.thatskyinteractions.data.TSIUserData;
import net.quepierts.thatskyinteractions.registry.Blocks;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class CandleClusterBlockRenderer implements StaticBlockEntityRenderer<CandleClusterBlockEntity> {
    private final ModelManager manager;

    public CandleClusterBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.manager = Minecraft.getInstance().getModelManager();
    }

    @Override
    public void render(
            @NotNull CandleClusterBlockEntity cluster,
            @NotNull StaticModelRenderer renderer,
            @NotNull BlockPos blockPos,
            @NotNull PoseStack poseStack
    ) {
        BlockState state = Blocks.CANDLE_CLUSTER.get().defaultBlockState();
        ShortArrayList candles = cluster.getCandles();

        poseStack.translate(-0.5f, 0, -0.5f);
        for (Short candle : candles) {
            CandleType type = CandleClusterBlockEntity.getCandleType(candle);
            float half = type.getSize() / 2f;
            float x = (CandleClusterBlockEntity.getCandleX(candle) + half) / 16.0f;
            float z = (CandleClusterBlockEntity.getCandleZ(candle) + half) / 16.0f;
            int rotation = CandleClusterBlockEntity.getCandleRotation(candle);
            boolean lit = CandleClusterBlockEntity.getCandleLit(candle);

            poseStack.pushPose();
            poseStack.translate(x, 0, z);

            BakedModel model = manager.getModel(CandleModels.get(type, lit));
            if (rotation != 0) {
                Matrix4f matrix4f = new Matrix4f()
                        .translate(0.5f, 0.0f, 0.5f)
                        .rotateY(CandleClusterBlockEntity.UNIT_ROTATION_RAD * rotation)
                        .translate(-0.5f, 0.0f, -0.5f);
                renderer.render(
                        RenderType.cutout(),
                        model,
                        state,
                        blockPos,
                        poseStack,
                        matrix4f,
                        false
                );
            } else {
                renderer.render(
                        RenderType.cutout(),
                        model,
                        state,
                        blockPos,
                        poseStack,
                        false
                );
            }

            poseStack.popPose();

            TSIUserData userData = ThatSkyInteractions.getInstance().getClient().getCache().getUserData();
            if (userData == null || userData.isPickedUp(cluster)) {
                return;
            }

            BlockPos pos = cluster.getBlockPos();
            if (Minecraft.getInstance().player == null)
                return;
            float distanceSqr = (float) Minecraft.getInstance().player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ());

            World2ScreenWidgetLayer.INSTANCE.addWorldPositionObject(cluster.getUUID(), cluster.provideW2SWidget(distanceSqr));
        }
    }
}
