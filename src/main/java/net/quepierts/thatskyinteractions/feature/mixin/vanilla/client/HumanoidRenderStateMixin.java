package net.quepierts.thatskyinteractions.feature.mixin.vanilla.client;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.quepierts.thatskyinteractions.feature.animation.HumanoidAnimationState;
import net.quepierts.thatskyinteractions.feature.client.render.HumanoidRenderStateExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(HumanoidRenderState.class)
public class HumanoidRenderStateMixin implements HumanoidRenderStateExtension {

    @Unique
    private HumanoidAnimationState a4j$AnimationState;

    @Unique
    @Override
    public HumanoidAnimationState a4j$GetAnimationState() {
        return this.a4j$AnimationState;
    }

    @Unique
    @Override
    public void a4j$SetAnimationState(final HumanoidAnimationState animationState) {
        this.a4j$AnimationState = animationState;
    }

}
