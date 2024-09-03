package net.quepierts.thatskyinteractions.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.simpleanimator.api.IAnimateHandler;
import net.quepierts.simpleanimator.api.animation.ModelBone;
import net.quepierts.simpleanimator.core.animation.Animator;
import net.quepierts.simpleanimator.core.client.ClientAnimator;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.util.EffectDistributorManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class PartPoseResolveLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public PartPoseResolveLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
        super(renderer);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int i, @NotNull AbstractClientPlayer player, float v, float v1, float v2, float v3, float v4, float partialTick) {
        Animator animator = ((IAnimateHandler) player).simpleanimator$getAnimator();

        if (!animator.isRunning()) {
            return;
        }

        ClientAnimator clientAnimator = (ClientAnimator) animator;
        PlayerModel<AbstractClientPlayer> parent = this.getParentModel();

        EffectDistributorManager distributorManager = ThatSkyInteractions.getInstance().getClient().getParticleDistributorManager();
        EffectDistributorManager.PartMatrices matrices = distributorManager.get(player.getUUID());
        // translate to world space
        float scale = player.getScale() * 0.9375F;
        Matrix4f root = matrices.root().set(new Matrix4f());
        root.translate((float) player.getX(), (float) player.getY(), (float) player.getZ());
        root.rotateY(player.yBodyRot * -Mth.DEG_TO_RAD);

        this.prepareRootTranslation(clientAnimator.getCache(ModelBone.ROOT), root);
        Matrix4f body = matrices.body().set(root);

        body.scale(-scale, scale, scale);
        body.translate(0, 1.501f, 0);

        this.translateToModelPart(parent.leftArm, matrices.leftArm().set(body));
        this.translateToModelPart(parent.rightArm, matrices.rightArm().set(body));
        /*this.translateToModelPart(parent.rightArm, matrix4f);
        matrix4f.translate(0.0625f, -0.5625f, 0.5f);

        Vector3f position = matrix4f.transformPosition(new Vector3f());
        player.level().addParticle(Particles.SHORTER_FLAME.get(), position.x, position.y, position.z, 0, 0, 0);*/
    }

    private void prepareRootTranslation(ClientAnimator.Cache cache, Matrix4f matrix4f) {
        Vector3f position = cache.position();
        matrix4f.translate(
                - position.x / 16f,
                position.y / 16f,
                position.z / 16f
        );

        Vector3f rotation = cache.rotation();
        if (rotation.x != 0 || rotation.y != 0 || rotation.z != 0) {
            matrix4f.rotateXYZ(rotation);
        }
    }

    private void translateToModelPart(ModelPart part, Matrix4f matrix4f) {
        matrix4f.translate(
                part.x / -16.0f,
                part.y / -16.0f,
                part.z / -16.0f
        );

        if (part.xRot != 0.0F || part.yRot != 0.0F || part.zRot != 0.0F) {
            matrix4f.rotateZYX(part.zRot, part.yRot, part.xRot);
        }
    }
}
