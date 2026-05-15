package net.quepierts.thatskyinteractions.feature.mixin.vanilla.client;

import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import net.quepierts.thatskyinteractions.feature.animation.HumanoidAnimationState;
import net.quepierts.thatskyinteractions.feature.client.render.HumanoidRenderStateExtension;
import net.quepierts.thatskyinteractions.feature.entity.AvatarExtension;
import net.quepierts.thatskyinteractions.feature.registry.DataComponents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public class AvatarRendererMixin<AvatarlikeEntity extends Avatar & ClientAvatarEntity> {

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
            at = @At("TAIL")
    )
    private void a4j$onExtractRenderState(
            final AvatarlikeEntity entity,
            final AvatarRenderState state,
            final float partialTicks,
            final CallbackInfo ci
    ) {
        final var animationState = ((AvatarExtension) entity).a4j$GetAnimationState();
        animationState.update(state.ageInTicks);
        ((HumanoidRenderStateExtension) state).a4j$SetAnimationState(animationState);
    }

}
