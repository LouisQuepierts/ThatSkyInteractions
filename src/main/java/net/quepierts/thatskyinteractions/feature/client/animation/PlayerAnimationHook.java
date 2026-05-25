package net.quepierts.thatskyinteractions.feature.client.animation;

import lombok.extern.slf4j.Slf4j;
import net.neoforged.fml.common.EventBusSubscriber;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftSkeletonPipeline;
import net.quepierts.thatskyinteractions.feature.animation.HumanoidAnimationState;
import net.quepierts.thatskyinteractions.feature.client.model.MinecraftModelAdaptor;

@Slf4j
@EventBusSubscriber
public final class PlayerAnimationHook {

    public static void onSetupAnimation(final HumanoidAnimationState animation, final MinecraftModelAdaptor adaptor) {
        if (!animation.isPlaying()) {
            return;
        }

        if (animation.isTicked()) {
            animation.getAnimation().resolve(
                    animation.getFsmState(),
                    animation.getExecutionState(),
                    animation,
                    adaptor.link(DefaultMinecraftSkeletonPipeline.MODIFIED_HUMANOID)
            );
        }

        adaptor.accept(animation.getCache());
    }

}
