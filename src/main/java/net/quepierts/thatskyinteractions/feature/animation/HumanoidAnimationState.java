package net.quepierts.thatskyinteractions.feature.animation;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftChannelLayout;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftSkeletonLayout;
import net.quepierts.thatskyinteractions.core.animation.parameter.ModelOverrideParameter;
import net.quepierts.veynir.backend.channel.ChannelFormat;
import net.quepierts.veynir.backend.channel.DefaultChannelFormats;
import net.quepierts.veynir.backend.uniform.UniformInstance;
import net.quepierts.veynir.core.AnimationState;
import net.quepierts.veynir.core.SkeletonState;
import net.quepierts.veynir.core.skeleton.ParentOverrideParameter;
import net.quepierts.veynir.core.skeleton.PivotModificationParameter;

@Slf4j
public final class HumanoidAnimationState extends AnimationState {

    @Getter
    private final SkeletonState skeleton    = new SkeletonState();
    @Getter
    private final UniformInstance<PivotModificationParameter> uboPivotModification;

    @Getter
    private final UniformInstance<ParentOverrideParameter> uboParentOverride;

    @Getter
    private final UniformInstance<ModelOverrideParameter> uboModelOverride;

    public static HumanoidAnimationState _default() {
        return new HumanoidAnimationState(DefaultChannelFormats.TIMELINE);
    }

    public HumanoidAnimationState(ChannelFormat channelFormat) {
        super(DefaultMinecraftChannelLayout.HUMANOID, channelFormat);

        final var parentOverrideParameter       = ParentOverrideParameter.of(DefaultMinecraftSkeletonLayout.HUMANOID);
        final var pivotModificationParameter    = PivotModificationParameter.of(DefaultMinecraftSkeletonLayout.HUMANOID);
        final var modelOverrideParameter        = ModelOverrideParameter.of(DefaultMinecraftSkeletonLayout.HUMANOID);

        pivotModificationParameter              .set("body", 0, 12, 0);
        pivotModificationParameter              .enable("body", true);

        this.uboPivotModification               = UniformInstance.of(pivotModificationParameter);
        this.uboParentOverride                  = UniformInstance.of(parentOverrideParameter);
        this.uboModelOverride                   = UniformInstance.of(modelOverrideParameter);

        this.uboPivotModification               .upload();
        this.uboParentOverride                  .upload();
    }

}
