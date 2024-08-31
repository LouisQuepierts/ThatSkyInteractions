package net.quepierts.thatskyinteractions.client.render;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.simpleanimator.api.IAnimateHandler;
import net.quepierts.simpleanimator.api.animation.ModelBone;
import net.quepierts.simpleanimator.api.animation.keyframe.VariableHolder;
import net.quepierts.simpleanimator.core.animation.Animator;
import net.quepierts.simpleanimator.core.client.ClientAnimator;
import net.quepierts.thatskyinteractions.client.registry.Particles;
import net.quepierts.thatskyinteractions.client.render.distributor.IEffectDistributor;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.EnumMap;

@OnlyIn(Dist.CLIENT)
public class EffectDistributeLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final EnumMap<ModelBone, ImmutableList<IEffectDistributor>> PARTICLE_DISTRIBUTORS;
    public EffectDistributeLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
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

        // translate to world space
        float scale = player.getScale() * 0.9375F;
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.translate((float) player.getX(), (float) player.getY(), (float) player.getZ());
        matrix4f.rotateY(player.yBodyRot * -Mth.DEG_TO_RAD);

        this.prepareRootTranslation(clientAnimator.getCache(ModelBone.ROOT), matrix4f);

        matrix4f.scale(-scale, scale, scale);
        matrix4f.translate(0, 1.501f, 0);

        this.onModelPart(clientAnimator, player, new Matrix4f(matrix4f), parent.leftArm, ModelBone.LEFT_ARM);
        this.onModelPart(clientAnimator, player, new Matrix4f(matrix4f), parent.rightArm, ModelBone.RIGHT_ARM);

        /*this.translateToModelPart(parent.rightArm, matrix4f);
        matrix4f.translate(0.0625f, -0.5625f, 0.5f);

        Vector3f position = matrix4f.transformPosition(new Vector3f());
        player.level().addParticle(Particles.SHORTER_FLAME.get(), position.x, position.y, position.z, 0, 0, 0);*/
    }

    private void prepareRootTranslation(ClientAnimator.Cache cache, Matrix4f matrix4f) {
        Vector3f position = cache.position();
        matrix4f.translate(
                - position.x,
                position.y,
                position.z
        );

        Vector3f rotation = cache.rotation();
        if (rotation.x != 0 || rotation.y != 0 || rotation.z != 0) {
            matrix4f.rotateXYZ(rotation);
        }
    }

    private void translateToModelPart(ModelPart part, PoseStack poseStack) {
        poseStack.translate(
                part.x / -16.0f,
                part.y / 16.0f,
                part.z / 16.0f
        );

        if (part.xRot != 0.0F || part.yRot != 0.0F || part.zRot != 0.0F) {
            poseStack.mulPose((new Quaternionf()).rotationZYX(part.zRot, part.yRot, part.xRot));
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

    private void onModelPart(ClientAnimator animator, AbstractClientPlayer player, Matrix4f matrix4f, ModelPart part, ModelBone bone) {
        ImmutableList<IEffectDistributor> distributors = PARTICLE_DISTRIBUTORS.get(bone);

        if (distributors == null || distributors.isEmpty())
            return;
        this.translateToModelPart(part, matrix4f);

        /*if (bone == ModelBone.LEFT_ARM || bone == ModelBone.RIGHT_ARM) {
            matrix4f.rotateX(-Mth.HALF_PI);
            matrix4f.rotateY(Mth.PI);
        }*/

        for (IEffectDistributor distributor : distributors) {
            VariableHolder holder = animator.getVariable(distributor.name());

            if (holder == VariableHolder.Immutable.ZERO || !distributor.shouldDistribute(holder))
                continue;

            Vector3f transformed = matrix4f.transformPosition(distributor.position(), new Vector3f());
            distributor.distribute(transformed, player, holder);
        }
    }

    static {
        PARTICLE_DISTRIBUTORS = new EnumMap<>(ModelBone.class);

        ImmutableList.Builder<IEffectDistributor> leftArm = ImmutableList.builder();
        PARTICLE_DISTRIBUTORS.put(ModelBone.LEFT_ARM, leftArm.build());

        ImmutableList.Builder<IEffectDistributor> rightArm = ImmutableList.builder();
        rightArm.add(EffectDistributors.CANDLE_FLAME_EFFECT_DISTRIBUTOR);
        PARTICLE_DISTRIBUTORS.put(ModelBone.RIGHT_ARM, rightArm.build());
    }
}
