package net.quepierts.thatskyinteractions.feature.client.animation;

import lombok.extern.slf4j.Slf4j;
import net.neoforged.fml.common.EventBusSubscriber;
import net.quepierts.thatskyinteractions.feature.animation.DefaultMinecraftAnimationPipeline;
import net.quepierts.thatskyinteractions.feature.animation.DefaultMinecraftSkeletonPipeline;
import net.quepierts.thatskyinteractions.feature.animation.HumanoidAnimationState;
import net.quepierts.thatskyinteractions.feature.client.model.MinecraftModelAdaptor;
import net.quepierts.thatskyinteractions.feature.client.model.MinecraftModelPoseProvider;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pass.definition.ParentOverridePassDefinition;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pass.definition.PivotPassDefinition;

@Slf4j
@EventBusSubscriber
public final class PlayerAnimationHook {

    public static void onSetupAnimation(final HumanoidAnimationState animation, final MinecraftModelAdaptor adaptor) {

        if (!animation.isPlaying()) {
            return;
        }

        if (animation.isTicked()) {

            final var pipeline = DefaultMinecraftAnimationPipeline.HUMANOID_TIMELINE;
            final var skeleton = DefaultMinecraftSkeletonPipeline.MODIFIED_HUMANOID;

            final var sampler = animation.getSampler();
            pipeline.bindSource("TimelineSampler", sampler);
            pipeline.submit(animation, skeleton.getAdapter());

            skeleton.bindUbo(PivotPassDefinition.REQUIRED_UBO, animation.getUboPivotModification().getBuffer());
            skeleton.bindUbo(ParentOverridePassDefinition.REQUIRED_UBO, animation.getUboParentOverride().getBuffer());
            skeleton.bindUbo(MinecraftModelPoseProvider.REQUIRED_UBO, animation.getUboModelOverride().getBuffer());
            skeleton.bindProvider(0, adaptor.link(DefaultMinecraftSkeletonPipeline.MODIFIED_HUMANOID));
            skeleton.bindTarget("Output", animation.getCache());
            skeleton.submit(animation.getSkeleton());
        }

        adaptor.accept(animation.getCache());
    }

}
