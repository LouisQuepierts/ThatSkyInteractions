package net.quepierts.thatskyinteractions.feature.mixin.vanilla.common;

import net.minecraft.world.entity.Avatar;
import net.quepierts.thatskyinteractions.feature.animation.HumanoidAnimationState;
import net.quepierts.thatskyinteractions.feature.entity.AvatarExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Avatar.class)
public class AvatarMixin implements AvatarExtension {

    @Unique
    private HumanoidAnimationState a4j$AnimationState;

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void a4j$init(final CallbackInfo ci) {
        this.a4j$AnimationState = HumanoidAnimationState._default();
    }

    @Unique
    @Override
    public HumanoidAnimationState a4j$GetAnimationState() {
        return this.a4j$AnimationState;
    }
}
