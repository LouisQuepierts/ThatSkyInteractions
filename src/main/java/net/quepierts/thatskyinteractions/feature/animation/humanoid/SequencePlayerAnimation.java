package net.quepierts.thatskyinteractions.feature.animation.humanoid;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.util.Mth;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerAnimationDefinition;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftAnimationPipeline;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftFSM;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftSkeletonPipeline;
import net.quepierts.thatskyinteractions.feature.animation.HumanoidAnimationState;
import net.quepierts.thatskyinteractions.feature.animation.bedrock.BedrockAnimationManager;
import net.quepierts.thatskyinteractions.feature.client.model.MinecraftModelPoseProvider;
import net.quepierts.thatskyinteractions.infra.animation.backend.execution.ExecutionState;
import net.quepierts.thatskyinteractions.infra.animation.backend.pipeline.AnimationPipeline;
import net.quepierts.thatskyinteractions.infra.animation.backend.sampler.AnimationSampler;
import net.quepierts.thatskyinteractions.infra.animation.backend.sampler.SamplingMode;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pass.definition.ParentOverridePassDefinition;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pass.definition.PivotPassDefinition;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline.SkeletonPipeline;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline.SkeletonPoseProvider;
import net.quepierts.thatskyinteractions.infra.animation.backend.source.AnimationSource;
import net.quepierts.thatskyinteractions.infra.animation.core.fsm.FSMState;
import org.jspecify.annotations.NonNull;

@Slf4j
public final class SequencePlayerAnimation extends BaseAnimation {

    private final AnimationPipeline     apl;
    private final SkeletonPipeline      spl;

    private final AnimationSampler      enter;
    private final AnimationSampler      main;
    private final AnimationSampler      exit;

    private final AnimationSampler[]    ordinal;

    public SequencePlayerAnimation(
            final AnimationPipeline     apl,
            final SkeletonPipeline      spl,
            final AnimationSource       enter,
            final AnimationSource       main,
            final AnimationSource       exit
    ) {
        super(DefaultMinecraftFSM.SEQUENCE);

        this.apl        = apl;
        this.spl        = spl;
        this.enter      = enter != null ? enter.link(apl) : null;
        this.main       = main != null ? main.link(apl) : null;
        this.exit       = exit != null ? exit.link(apl) : null;

        this.ordinal    = new AnimationSampler[] {
                        this.enter,
                        this.main,
                        this.exit
        };
    }

    public static PlayerAnimation parse(final @NonNull PlayerAnimationDefinition definition) {
        final var manager   = BedrockAnimationManager.getInstance();

        final var sources   = definition.sources();
        final var enter     = parse(sources.get("enter"), manager);
        final var main      = parse(sources.get("main"), manager);
        final var exit      = parse(sources.get("exit"), manager);

        final var animation = new SequencePlayerAnimation(
                DefaultMinecraftAnimationPipeline.HUMANOID_TIMELINE,
                DefaultMinecraftSkeletonPipeline.MODIFIED_HUMANOID,
                enter,
                main,
                exit
        );

        setParameter(1, animation.uniform, sources.get("enter"), enter);
        setParameter(2, animation.uniform, sources.get("main"), main);
        setParameter(3, animation.uniform, sources.get("exit"), exit);

        return animation;
    }

    @Override
    public void resolve(
            @NonNull final FSMState fsmState,
            @NonNull final ExecutionState executionState,
            @NonNull final HumanoidAnimationState animationState,
            @NonNull final SkeletonPoseProvider provider
    ) {
        final var animation = this.apl;
        final var skeleton  = this.spl;

        final var mode          = this.extractSamplingMode(fsmState);

        animation.setSamplingMode(1, mode);
        animation.bindSource(1, this.ordinal[Mth.clamp(
                fsmState.getCurrentState() - 1,
                0, 2
        )]);
        animation.submit(animationState, skeleton.getAdapter());

        skeleton.bindUbo(PivotPassDefinition.REQUIRED_UBO, animationState.getUboPivotModification().getBuffer());
        skeleton.bindUbo(ParentOverridePassDefinition.REQUIRED_UBO, animationState.getUboParentOverride().getBuffer());
        skeleton.bindUbo(MinecraftModelPoseProvider.REQUIRED_UBO, animationState.getUboModelOverride().getBuffer());
        skeleton.bindProvider(0, provider);
        skeleton.bindTarget("Output", animationState.getCache());
        skeleton.submit(animationState.getSkeleton());
    }

    private SamplingMode extractSamplingMode(final @NonNull FSMState state) {

        final var transition = state.isBlending();

        final var currentState = state.getCurrentState();
        if (currentState == 0 || state.getLastState() == 0 && transition) {
            return SamplingMode.FREEZE_START;
        } else if (state.getLastState() == 3 && transition) {
            return SamplingMode.FREEZE_END;
        }

        return SamplingMode.DEFAULT;

    }

}
