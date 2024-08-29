package net.quepierts.thatskyinteractions.client.ber;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.block.entity.WingOfLightBlockEntity;
import net.quepierts.thatskyinteractions.client.gui.layer.World2ScreenWidgetLayer;
import net.quepierts.thatskyinteractions.client.registry.PostEffects;
import net.quepierts.thatskyinteractions.client.registry.RenderTypes;
import org.lwjgl.opengl.GL11;

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
    public void render(WingOfLightBlockEntity wingOfLightBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLight, int combinedOverlay) {
        BlockPos pos = wingOfLightBlockEntity.getBlockPos();
        float distanceSqr = (float) minecraft.player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ());

        //RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT, Minecraft.ON_OSX);
//        PostEffects.getBloomTarget().bindWrite(true);

        World2ScreenWidgetLayer.INSTANCE.addWorldPositionObject(wingOfLightBlockEntity.getUUID(), wingOfLightBlockEntity.provideW2SWidget(distanceSqr));
        poseStack.pushPose();
        poseStack.translate(0.5, 1.45, 0.5);
        poseStack.scale(-0.95F, -0.95F, 0.95F);
        poseStack.mulPose(Axis.YN.rotation(wingOfLightBlockEntity.getYRot()));

        playerModel.young = false;
        playerModel.head.xRot = wingOfLightBlockEntity.getXRot();
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderTypes.BLOOM);
        playerModel.renderToBuffer(poseStack, vertexConsumer, combinedLight, combinedOverlay);

//        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);

        poseStack.popPose();
    }

    @Override
    public AABB getRenderBoundingBox(WingOfLightBlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
    }
}
