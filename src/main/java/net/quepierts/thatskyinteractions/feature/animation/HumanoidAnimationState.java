package net.quepierts.thatskyinteractions.feature.animation;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftChannelLayout;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftSkeletonLayout;
import net.quepierts.thatskyinteractions.feature.animation.humanoid.PlayerAnimation;
import net.quepierts.thatskyinteractions.core.animation.parameter.ModelOverrideParameter;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.ChannelFormat;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.DefaultChannelFormats;
import net.quepierts.thatskyinteractions.infra.animation.backend.execution.ExecutionState;
import net.quepierts.thatskyinteractions.infra.animation.backend.uniform.UniformInstance;
import net.quepierts.thatskyinteractions.infra.animation.core.AnimationState;
import net.quepierts.thatskyinteractions.infra.animation.core.SkeletonState;
import net.quepierts.thatskyinteractions.infra.animation.core.fsm.FSMState;
import net.quepierts.thatskyinteractions.infra.animation.core.skeleton.ParentOverrideParameter;
import net.quepierts.thatskyinteractions.infra.animation.core.skeleton.PivotModificationParameter;
import net.quepierts.thatskyinteractions.infra.animation.core.skeleton.PoseCache;

@Slf4j
public final class HumanoidAnimationState extends AnimationState {

    @Getter
    private final SkeletonState skeleton    = new SkeletonState(); // dummy

    @Getter
    private final FSMState      fsmState    = new FSMState();

    @Getter
    private final ExecutionState executionState = new ExecutionState(64);

    @Getter
    private final PoseCache     cache       = new PoseCache(DefaultMinecraftSkeletonLayout.HUMANOID);

    @Getter
    private final UniformInstance<PivotModificationParameter> uboPivotModification;

    @Getter
    private final UniformInstance<ParentOverrideParameter> uboParentOverride;

    @Getter
    private final UniformInstance<ModelOverrideParameter> uboModelOverride;

    @Getter
    private Identifier          current;

    @Getter
    private PlayerAnimation     animation;

    @Getter
    private boolean             playing;

    private int last;

    @Getter
    private boolean ticked = false;

    @Getter
    private boolean resolved = false;

    @Getter
    private float alpha = 0.0f;

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

    public void play(Identifier identifier) {
        final var animation = PlayerAnimationManager
                            .getInstance()
                            .get(identifier);

        if (animation == null) {
            log.warn("Animation source not found: {}", identifier);
            return;
        }

        this.current        = identifier;
        this.animation = animation;

        this.playing        = true;
        this.progress       = 0.0f;
    }

    public void tick(int current) {
        if (this.playing) {
            final var delta = (current - last) * 0.05f;
            this.ticked     = current != last;

            this.animation  .update(this.fsmState, delta);
            this.progress   = this.fsmState.getElapsed();

            if (this.fsmState.isFinished()) {
                this.animation.cleanup(this.fsmState);
                this.playing = false;
                this.current = null;
                this.animation = null;
            }
        }

        this.last       = current;
    }

    public void update(float partialTicks) {
        if (this.playing) {
            this.resolved   = false;
            final var delta = partialTicks * 0.05f;
            this.progress               = this.fsmState.getElapsed() + delta;
            var progress                = Math.min(
                    (this.fsmState.getBlendElapsed() + delta) / this.fsmState.getBlendDuration(),
                    1.0f
            );
            this.alpha                  = getAlpha(this.fsmState, progress);
        }
    }



    private float getAlpha(final FSMState fsmState, float progress) {
        var alpha = 1.0f;

        final var current = this.getAnimation();
        final var fsm = current.getFsm();
        final var lookup = fsm.getLookup();

        if (fsmState.getCurrentState() == 0) {
            alpha = 0.0f;
        } else if ("system#exit".equals(lookup.name(fsmState.getCurrentState()))) {
            alpha = 1.0f - progress;
        } else if ("system#enter".equals(lookup.name(fsmState.getLastState()))) {
            alpha = progress;
        }

        return alpha;
    }

    public void markResolved() {
        this.resolved = true;
    }
}
