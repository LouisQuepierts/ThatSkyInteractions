package net.quepierts.thatskyinteractions.feature.mixin.vanilla.client;

import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.quepierts.thatskyinteractions.feature.animation.HumanoidAnimationState;
import net.quepierts.thatskyinteractions.feature.client.render.AvatarRenderStateExtension;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public class AvatarRenderStateMixin implements AvatarRenderStateExtension {

    @Unique
    @Final
    private final HumanoidAnimationState a4j$AnimationState = HumanoidAnimationState._default();

    @Unique
    @Override
    public HumanoidAnimationState a4j$GetAnimationState() {
        return this.a4j$AnimationState;
    }
}
