package net.quepierts.thatskyinteractions.feature.animation;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.feature.client.animation.ClientAnimationManager;
import net.quepierts.thatskyinteractions.feature.client.model.ModelOverrideParameter;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.ChannelFormat;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.DefaultChannelFormats;
import net.quepierts.thatskyinteractions.infra.animation.backend.sampler.AnimationSampler;
import net.quepierts.thatskyinteractions.infra.animation.backend.source.AnimationSource;
import net.quepierts.thatskyinteractions.infra.animation.backend.uniform.UniformInstance;
import net.quepierts.thatskyinteractions.infra.animation.core.AnimationState;
import net.quepierts.thatskyinteractions.infra.animation.core.SkeletonState;
import net.quepierts.thatskyinteractions.infra.animation.core.skeleton.ParentOverrideParameter;
import net.quepierts.thatskyinteractions.infra.animation.core.skeleton.PivotModificationParameter;
import net.quepierts.thatskyinteractions.infra.animation.core.skeleton.PoseCache;

@Slf4j
public final class HumanoidAnimationState extends AnimationState {

    @Getter
    private final SkeletonState skeleton    = new SkeletonState(); // dummy

    @Getter
    private final PoseCache     cache       = new PoseCache(DefaultMinecraftSkeletonLayout.HUMANOID);

    @Getter
    private final UniformInstance<PivotModificationParameter> uboPivotModification;

    @Getter
    private final UniformInstance<ParentOverrideParameter> uboParentOverride;

    @Getter
    private final UniformInstance<ModelOverrideParameter> uboModelOverride;


    private Identifier          current;
    private AnimationSource     source;

    @Getter
    private AnimationSampler    sampler;

    @Getter
    private boolean             playing;
    private boolean             loop;

    private float last;

    @Getter
    private boolean ticked = false;

    public static HumanoidAnimationState _default() {
        return new HumanoidAnimationState(DefaultChannelFormats.TIMELINE);
    }

    public HumanoidAnimationState(ChannelFormat channelFormat) {
        super(DefaultMinecraftChannelLayout.HUMANOID, channelFormat);

        final var parentOverrideParameter       = ParentOverrideParameter.of(DefaultMinecraftSkeletonLayout.HUMANOID);
        final var pivotModificationParameter    = PivotModificationParameter.of(DefaultMinecraftSkeletonLayout.HUMANOID);
        final var modelOverrideParameter        = ModelOverrideParameter.of(DefaultMinecraftSkeletonLayout.HUMANOID);

        parentOverrideParameter                 .setData(DefaultMinecraftSkeletonLayout.MODIFIED_PO);

        pivotModificationParameter              .set("body", 0, 12, 0);
        pivotModificationParameter              .enable("body", true);

        this.uboPivotModification               = UniformInstance.of(pivotModificationParameter);
        this.uboParentOverride                  = UniformInstance.of(parentOverrideParameter);
        this.uboModelOverride                   = UniformInstance.of(modelOverrideParameter);

        this.uboPivotModification               .upload();
        this.uboParentOverride                  .upload();
    }

    public void play(Identifier identifier) {
        final var source    = ClientAnimationManager
                            .getInstance()
                            .get(identifier);

        if (source == null) {
            log.warn("Animation source not found: {}", identifier);
            return;
        }

        this.current        = identifier;
        this.source         = source;
        this.sampler        = source.link(DefaultMinecraftAnimationPipeline.HUMANOID_TIMELINE);

        this.playing        = true;
        this.progress       = 0.0f;
    }

    public void update(float current) {
        // for test
        if (this.playing) {
            final var delta = (current - last) * 0.05f;
            this.ticked     = current != last;
            this.progress   = this.progress + delta /*% 3.0f*/;


            final var duration = this.source.getDuration();
            if (this.progress > duration) {
                if (this.loop) {
                    this.progress = this.progress % duration;
                } else {
                    this.playing   = false;
                    this.progress  = duration;

                    this.source     = null;
                    this.sampler    = null;
                    this.current    = null;
                }
            }
        }

        this.last       = current;
    }
}
