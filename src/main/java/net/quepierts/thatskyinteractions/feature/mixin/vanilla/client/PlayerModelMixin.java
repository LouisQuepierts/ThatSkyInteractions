package net.quepierts.thatskyinteractions.feature.mixin.vanilla.client;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.quepierts.thatskyinteractions.feature.animation.DefaultMinecraftAnimationPipeline;
import net.quepierts.thatskyinteractions.feature.animation.DefaultMinecraftChannelLayout;
import net.quepierts.thatskyinteractions.feature.client.model.MinecraftModelAdaptor;
import net.quepierts.thatskyinteractions.feature.client.render.AvatarRenderStateExtension;
import net.quepierts.thatskyinteractions.feature.client.render.MinecraftModelAdaptorProvider;
import net.quepierts.thatskyinteractions.infra.animation.backend.pipeline.AnimationPipeline;
import net.quepierts.thatskyinteractions.infra.animation.backend.sampler.AnimationSampler;
import net.quepierts.thatskyinteractions.infra.animation.backend.source.AnimationSource;
import net.quepierts.thatskyinteractions.test.animation.AnimationTest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public class PlayerModelMixin {

    @Unique
    private MinecraftModelAdaptor a4j$ModelAdaptor;

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void a4j$init(final ModelPart root, final boolean slim, final CallbackInfo ci) {
        this.a4j$ModelAdaptor   = MinecraftModelAdaptor.auto(root, DefaultMinecraftChannelLayout.HUMANOID);
    }

    @Inject(
            method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/HumanoidModel;setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V",
                    shift = At.Shift.AFTER
            )
    )
    public void tsi$setupAnim(final AvatarRenderState state, final CallbackInfo ci) {
        // do animation thing

        final var pipeline  = DefaultMinecraftAnimationPipeline.HUMANOID_TIMELINE;
        final var sampler   = AnimationTest.getSampler();

        final var animation = ((AvatarRenderStateExtension) state).a4j$GetAnimationState();

        animation.update(state.ageInTicks);

        pipeline.bindSource("TimelineSampler", sampler);
        pipeline.submit(animation, this.a4j$ModelAdaptor);

    }
}
