package net.quepierts.thatskyinteractions.feature.mixin.vanilla.client;

import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AvatarRenderer.class)
public class AvatarRendererMixin<AvatarlikeEntity extends Avatar & ClientAvatarEntity> {

    /*@Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
            at = @At("TAIL")
    )
    private void a4j$onExtractRenderState(
            final AvatarlikeEntity entity,
            final AvatarRenderState state,
            final float partialTicks,
            final CallbackInfo ci
    ) {

        final var data          = PlayerAnimationSystem.getAnimationData(entity);
        final var animation     = data.getAnimation();
        animation               .update(partialTicks);

        state                   .setRenderData(RenderDataTypes.ANIMATION_STATE, animation);
    }*/

}
