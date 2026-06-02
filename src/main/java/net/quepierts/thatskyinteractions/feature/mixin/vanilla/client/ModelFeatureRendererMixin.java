package net.quepierts.thatskyinteractions.feature.mixin.vanilla.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.neoforged.neoforge.client.extensions.IRenderStateExtension;
import net.quepierts.thatskyinteractions.feature.animation.HumanoidAnimationState;
import net.quepierts.thatskyinteractions.feature.client.animation.PlayerAnimationHook;
import net.quepierts.thatskyinteractions.feature.client.renderstate.AnimationStateModifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelFeatureRenderer.class)
public class ModelFeatureRendererMixin {

    @Shadow
    @Final
    private PoseStack poseStack;

    @Inject(
            method = "renderModel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/Model;setupAnim(Ljava/lang/Object;)V",
                    shift = At.Shift.AFTER
            )
    )
    private <S> void a4j$renderModel(
            final SubmitNodeStorage.ModelSubmit<S>  submit,
            final RenderType                        renderType,
            final VertexConsumer                    buffer,
            final OutlineBufferSource               outlineBufferSource,
            final MultiBufferSource.BufferSource    crumblingBufferSource,
            final CallbackInfo                      ci
    ) {
        PlayerAnimationHook.onSetupRootAnimation(
                submit,
                this.poseStack
        );
    }

}
