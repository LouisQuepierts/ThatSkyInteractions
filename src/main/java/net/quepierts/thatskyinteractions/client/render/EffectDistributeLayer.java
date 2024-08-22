package net.quepierts.thatskyinteractions.client.render;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.quepierts.simpleanimator.api.IAnimateHandler;
import net.quepierts.simpleanimator.api.animation.ModelBone;
import net.quepierts.simpleanimator.api.animation.keyframe.VariableHolder;
import net.quepierts.simpleanimator.core.animation.Animator;
import net.quepierts.simpleanimator.core.client.ClientAnimator;
import net.quepierts.thatskyinteractions.client.render.distributor.IEffectDistributor;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.EnumMap;

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

        Vector3f position = player.position().toVector3f();
        Vector3f rotation = new Vector3f(
                0,
                player.yBodyRot * -Mth.DEG_TO_RAD,
                0
        );

        ClientAnimator clientAnimator = (ClientAnimator) animator;
        ClientAnimator.Cache root = clientAnimator.getCache(ModelBone.ROOT);
        position.add(root.position().x / 16.0f, root.position().y / 16.0f, root.position().z / 16.0f);
        rotation.add(root.rotation());

        PlayerModel<AbstractClientPlayer> model = this.getParentModel();
        Matrix4f matrix4f = new Matrix4f()
                .translate(position)
                .rotateXYZ(rotation)
                .translate(0, 1.5f, 0);

        onModelPart(clientAnimator, player, new Matrix4f(matrix4f), model.body, ModelBone.BODY);
        onModelPart(clientAnimator, player, new Matrix4f(matrix4f), model.leftArm, ModelBone.LEFT_ARM);
        onModelPart(clientAnimator, player, new Matrix4f(matrix4f), model.rightArm, ModelBone.RIGHT_ARM);

    }

    private void onModelPart(ClientAnimator animator, AbstractClientPlayer player, Matrix4f matrix4f, ModelPart part, ModelBone bone) {
        ImmutableList<IEffectDistributor> distributors = PARTICLE_DISTRIBUTORS.get(bone);

        if (distributors == null || distributors.isEmpty())
            return;
        matrix4f.translate(
                        part.x / 16.0f,
                        part.y / -16.0f,
                        part.z / -16.0f
                ).rotateXYZ(
                        part.xRot,
                        -part.yRot,
                        -part.zRot
                );

        if (bone == ModelBone.LEFT_ARM || bone == ModelBone.RIGHT_ARM) {
            matrix4f.rotateX(-Mth.HALF_PI);
            matrix4f.rotateY(Mth.PI);
        }


        for (IEffectDistributor distributor : distributors) {
            VariableHolder holder = animator.getVariable(distributor.name());

            if (holder == VariableHolder.ZERO)
                continue;

            Vector3f transformed = matrix4f.transformPosition(distributor.position(), new Vector3f());
            distributor.distribute(transformed, player, holder);
        }

        Vector3f translation = matrix4f.getTranslation(new Vector3f());
        player.level().addParticle(ParticleTypes.END_ROD, translation.x, translation.y, translation.z, 0, 0, 0);
        matrix4f.translate(0, 0, 1f);
        translation = matrix4f.getTranslation(translation);
        player.level().addParticle(ParticleTypes.END_ROD, translation.x, translation.y, translation.z, 0, 0, 0);
        matrix4f.translate(0, 0, -2f);
        translation = matrix4f.getTranslation(translation);
        player.level().addParticle(ParticleTypes.END_ROD, translation.x, translation.y, translation.z, 0, 0, 0);

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
