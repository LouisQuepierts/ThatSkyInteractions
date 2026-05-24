package net.quepierts.thatskyinteractions.feature.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.experimental.UtilityClass;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.quepierts.thatskyinteractions.feature.animation.HumanoidAnimationState;

@UtilityClass
public class LivingEntityRendererHook {

    public static void onBeforeRender(
            final HumanoidAnimationState    state,
            final PoseStack                 poseStack,
            final SubmitNodeCollector       submitNodeCollector,
            final CameraRenderState         camera
    ) {
        if (state == null || !state.isPlaying()) {
            return;
        }

        final var cache     = state.getCache();
        final var root      = cache.get(0);

        poseStack.translate(
                root.getTx() * 0.0625f,
                root.getTy() * 0.0625f,
                root.getTz() * 0.0625f
        );
    }

}
