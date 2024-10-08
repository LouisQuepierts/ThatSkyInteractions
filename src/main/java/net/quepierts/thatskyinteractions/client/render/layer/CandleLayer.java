package net.quepierts.thatskyinteractions.client.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.quepierts.simpleanimator.api.IAnimateHandler;
import net.quepierts.simpleanimator.core.animation.Animator;
import net.quepierts.simpleanimator.core.client.ClientAnimator;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class CandleLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final BlockState CANDLE = Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.LIT, true);
    private final BlockRenderDispatcher blockRenderer;

    public CandleLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer, BlockRenderDispatcher blockRenderer) {
        super(renderer);
        this.blockRenderer = blockRenderer;
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int packedLight, @NotNull AbstractClientPlayer player, float v, float v1, float v2, float v3, float v4, float v5) {
        Animator animator = ((IAnimateHandler) player).simpleanimator$getAnimator();

        if (!animator.isRunning()) {
            return;
        }

        ClientAnimator clientAnimator = (ClientAnimator) animator;
        if (clientAnimator.getVariable("heldCandle").getAsBoolean()) {
            int overlayCoords = LivingEntityRenderer.getOverlayCoords(player, 0.0F);
            poseStack.pushPose();
            PlayerModel<AbstractClientPlayer> parent = this.getParentModel();
            parent.translateToHand(HumanoidArm.RIGHT, poseStack);

            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            poseStack.translate(-0.4375F, 0.125F, -1.0F);
            this.blockRenderer.renderSingleBlock(
                    CANDLE,
                    poseStack,
                    multiBufferSource,
                    packedLight,
                    overlayCoords,
                    ModelData.EMPTY,
                    null
            );

            poseStack.popPose();
        }
    }
}
