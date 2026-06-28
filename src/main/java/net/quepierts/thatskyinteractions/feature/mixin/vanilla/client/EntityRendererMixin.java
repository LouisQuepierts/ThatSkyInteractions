package net.quepierts.thatskyinteractions.feature.mixin.vanilla.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.client.renderstate.AnimationStateModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {

    @Inject(
            method = "createRenderState(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;",
            at = @At("TAIL")
    )
    private void a4j$createRenderState(
            final T                         entity,
            final float                     partialTicks,
            final CallbackInfoReturnable<S> cir
    ) {

        final var animatable = PlayerAnimationSystem.tryParseAnimatable(entity);
        if (animatable != null) {
            final var state = cir.getReturnValue();
            if (state.getClass() == AvatarRenderState.class) {

                AnimationStateModifier.INSTANCE.accept(animatable, (AvatarRenderState) state);

            }
        }

    }

}
