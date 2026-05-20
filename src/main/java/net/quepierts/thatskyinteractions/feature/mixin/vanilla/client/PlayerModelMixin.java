package net.quepierts.thatskyinteractions.feature.mixin.vanilla.client;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.quepierts.thatskyinteractions.feature.animation.DefaultMinecraftSkeletonLayout;
import net.quepierts.thatskyinteractions.feature.client.animation.PlayerAnimationHook;
import net.quepierts.thatskyinteractions.feature.client.model.MinecraftModelAdaptor;
import net.quepierts.thatskyinteractions.feature.client.model.MinecraftModelSkeleton;
import net.quepierts.thatskyinteractions.feature.client.render.HumanoidRenderStateExtension;
import net.quepierts.thatskyinteractions.feature.client.render.EntityModelExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public class PlayerModelMixin implements EntityModelExtension {

    @Unique
    private MinecraftModelAdaptor a4j$ModelAdaptor;

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void a4j$init(final ModelPart root, final boolean slim, final CallbackInfo ci) {
        final var skeleton = MinecraftModelSkeleton.auto(root, DefaultMinecraftSkeletonLayout.HUMANOID);
        this.a4j$ModelAdaptor = MinecraftModelAdaptor.of(skeleton);
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
        final var animation = ((HumanoidRenderStateExtension) state).a4j$GetAnimationState();
        PlayerAnimationHook.onSetupAnimation(animation, this.a4j$ModelAdaptor);

    }

    @Unique
    @Override
    public MinecraftModelAdaptor a4j$GetModelAdaptor() {
        return this.a4j$ModelAdaptor;
    }
}
