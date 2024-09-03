package net.quepierts.thatskyinteractions.client.render.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.block.entity.WingOfLightBlockEntity;
import net.quepierts.thatskyinteractions.client.gui.layer.World2ScreenWidgetLayer;
import net.quepierts.thatskyinteractions.client.registry.PostEffects;
import net.quepierts.thatskyinteractions.client.registry.RenderTypes;
import net.quepierts.thatskyinteractions.data.TSIUserData;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class WingOfLightBlockRenderer implements BlockEntityRenderer<WingOfLightBlockEntity> {
    private final PlayerModel<AbstractClientPlayer> playerModel;
    private final Minecraft minecraft;
    public WingOfLightBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.playerModel = new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false);
        this.playerModel.young = false;
        this.minecraft = Minecraft.getInstance();
    }

    @Override
    public void render(
            @NotNull WingOfLightBlockEntity wingOfLightBlockEntity,
            float partialTick,
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource multiBufferSource,
            int combinedLight,
            int combinedOverlay
    ) {
        TSIUserData userData = ThatSkyInteractions.getInstance().getClient().getCache().getUserData();

        if (userData.isPickedUp(wingOfLightBlockEntity)) {
            return;
        }

        BlockPos pos = wingOfLightBlockEntity.getBlockPos();

        if (minecraft.player == null)
            return;
        float distanceSqr = (float) minecraft.player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ());

        World2ScreenWidgetLayer.INSTANCE.addWorldPositionObject(wingOfLightBlockEntity.getUUID(), wingOfLightBlockEntity.provideW2SWidget(distanceSqr));
        poseStack.pushPose();
        poseStack.translate(0.5, 1.45, 0.5);
        poseStack.scale(-0.95F, -0.95F, 0.95F);
        poseStack.mulPose(Axis.YP.rotation(wingOfLightBlockEntity.getYRot()));

        playerModel.young = false;
        playerModel.head.xRot = wingOfLightBlockEntity.getXRot();
        VertexConsumer vertexConsumer = RenderTypes.getBufferSource().getBuffer(RenderTypes.WOL);
        playerModel.renderToBuffer(poseStack, vertexConsumer, combinedLight, combinedOverlay);

        PostEffects.setApplyBloom();
        poseStack.popPose();
    }

    @NotNull
    @Override
    public AABB getRenderBoundingBox(WingOfLightBlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
    }
}
