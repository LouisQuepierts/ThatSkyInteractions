package net.quepierts.thatskyinteractions.feature.client.renderstate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.LivingEntity;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationController;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import org.jspecify.annotations.NonNull;

public class AnimationStateModifier {

    public static final AnimationStateModifier                  INSTANCE
            = new AnimationStateModifier();

    public static final ContextKey<PlayerAnimationController>   CONTEXT_KEY
            = RenderStateModifiers.create("animation_state");

    public void accept(
            final @NonNull LivingEntity         animatable,
            final @NonNull AvatarRenderState    renderState
    ) {
        final var data          = PlayerAnimationSystem.getAnimationData(animatable);
        final var controller    = data.getController();

        final var minecraft     = Minecraft.getInstance();
        final var tracker       = minecraft.getDeltaTracker();
        final var manager       = animatable.level().tickRateManager();

        final var frozen        = manager.isEntityFrozen(animatable);
        final var delta         = tracker.getGameTimeDeltaPartialTick(!frozen);

        controller.update(delta);
        controller.setupToLocal(renderState);
        renderState.setRenderData(
                AnimationStateModifier.CONTEXT_KEY,
                controller
        );
    }
}
