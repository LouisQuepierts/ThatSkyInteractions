package net.quepierts.thatskyinteractions.feature.mixin.vanilla.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationController;
import net.quepierts.thatskyinteractions.feature.client.animation.PlayerAnimationHook;
import net.quepierts.thatskyinteractions.feature.client.renderstate.AnimationStateModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<S extends LivingEntityRenderState> {

    @Inject(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Ljava/lang/Object;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void a4j$applyRootAnimation(
            final S                     state,
            final PoseStack             poseStack,
            final SubmitNodeCollector   submitNodeCollector,
            final CameraRenderState     camera,
            final CallbackInfo          ci
    ) {
        PlayerAnimationHook.onSetupRootAnimation(
                state,
                poseStack
        );
    }

}
