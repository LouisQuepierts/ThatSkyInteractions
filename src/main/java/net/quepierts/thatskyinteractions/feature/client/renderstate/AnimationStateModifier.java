package net.quepierts.thatskyinteractions.feature.client.renderstate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Avatar;
import net.neoforged.neoforge.client.renderstate.AvatarRenderStateModifier;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.animation.HumanoidAnimationState;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationController;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import org.jspecify.annotations.NonNull;

public class AnimationStateModifier extends AvatarRenderStateModifier {

    public static final AnimationStateModifier                  INSTANCE
            = new AnimationStateModifier();

    public static final ContextKey<PlayerAnimationController>   CONTEXT_KEY
            = RenderStateModifiers.create("animation_state");

    @Override
    public <T extends Avatar & ClientAvatarEntity> void accept(
            final T avatar,
            final @NonNull AvatarRenderState renderState
    ) {
        final var data          = PlayerAnimationSystem.getAnimationData(avatar);
        final var controller    = data.getController();

        final var minecraft = Minecraft.getInstance();
        final var tracker = minecraft.getDeltaTracker();
        final var manager = avatar.level().tickRateManager();

        final var frozen = manager.isEntityFrozen(avatar);
        final var delta = tracker.getGameTimeDeltaPartialTick(!frozen);

        controller.update(delta);
        renderState.setRenderData(
                AnimationStateModifier.CONTEXT_KEY,
                controller
        );
    }
}
