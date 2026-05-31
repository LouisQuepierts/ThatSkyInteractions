package net.quepierts.thatskyinteractions.feature.mixin.vanilla.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.quepierts.thatskyinteractions.feature.client.renderstate.AnimationStateModifier;
import net.quepierts.thatskyinteractions.feature.client.render.LivingEntityRendererHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {

    @Shadow
    public abstract M getModel();

    @Inject(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getRenderType(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;ZZZ)Lnet/minecraft/client/renderer/rendertype/RenderType;",
                    shift = At.Shift.AFTER
            )
    )
    private void aj4$animate(
            final S state,
            final PoseStack poseStack,
            final SubmitNodeCollector submitNodeCollector,
            final CameraRenderState camera,
            final CallbackInfo ci
    ) {

        final var animation = state.getRenderData(AnimationStateModifier.CONTEXT_KEY);
        if (animation != null) {
            LivingEntityRendererHook.onBeforeRender(
                    animation,
                    poseStack,
                    submitNodeCollector,
                    camera
            );
        }

    }

}
