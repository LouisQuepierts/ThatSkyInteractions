package net.quepierts.thatskyinteractions.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.quepierts.simpleanimator.api.IAnimateHandler;
import net.quepierts.simpleanimator.core.animation.Animator;
import net.quepierts.simpleanimator.core.client.ClientAnimator;
import net.quepierts.thatskyinteractions.client.registry.Particles;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class CandleLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final BlockState CANDLE = Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.LIT, true);
    private final BlockRenderDispatcher blockRenderer;

    public CandleLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer, BlockRenderDispatcher blockRenderer) {
        super(renderer);
        this.blockRenderer = blockRenderer;
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int packedLight, AbstractClientPlayer player, float v, float v1, float v2, float v3, float v4, float v5) {
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
            //addParticlesAndSound(player.level(), this.getPlayerHandPos(player, v5), player.getRandom());
        }
    }

    private Vector3f getPlayerHandPos(Player player, float partialTick) {
        ModelPart rightArm = this.getParentModel().rightArm;
        Matrix4f mat = new Matrix4f()
                .translate(player.getEyePosition().toVector3f())
                .rotateY(Mth.lerp(partialTick, player.yBodyRotO, player.yBodyRot) * -Mth.DEG_TO_RAD)
                .translate(rightArm.x / 16f, rightArm.y / 16f, rightArm.z / 16f)
                .rotateXYZ(rightArm.xRot, rightArm.yRot, rightArm.zRot);

        return mat.getTranslation(new Vector3f());
    }

    private static void addParticlesAndSound(Level level, Vector3f offset, RandomSource random) {
        float f = random.nextFloat();
        if (f < 0.3F) {
            //level.addParticle(ParticleTypes.SMOKE, offset.x, offset.y, offset.z, 0.0, 0.0, 0.0);
            if (f < 0.17F) {
                level.playLocalSound(
                        offset.x + 0.5,
                        offset.y + 0.5,
                        offset.z + 0.5,
                        SoundEvents.CANDLE_AMBIENT,
                        SoundSource.PLAYERS,
                        1.0F + random.nextFloat(),
                        random.nextFloat() * 0.7F + 0.3F,
                        false
                );
            }
        }

        level.addParticle(Particles.SHORTER_FLAME.get(), offset.x, offset.y, offset.z, 0.0, 0.0, 0.0);
    }
}
