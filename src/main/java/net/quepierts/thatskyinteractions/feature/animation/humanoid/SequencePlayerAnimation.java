package net.quepierts.thatskyinteractions.feature.animation.humanoid;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.core.model.animation.PlayerAnimationDefinition;
import net.quepierts.thatskyinteractions.core.model.animation.SourceDefinition;
import net.quepierts.thatskyinteractions.feature.animation.DefaultMinecraftAnimationPipeline;
import net.quepierts.thatskyinteractions.feature.animation.DefaultMinecraftFSM;
import net.quepierts.thatskyinteractions.feature.animation.DefaultMinecraftSkeletonPipeline;
import net.quepierts.thatskyinteractions.feature.animation.HumanoidAnimationState;
import net.quepierts.thatskyinteractions.feature.animation.bedrock.BedrockAnimationCompiler;
import net.quepierts.thatskyinteractions.feature.animation.bedrock.BedrockAnimationManager;
import net.quepierts.thatskyinteractions.feature.client.model.MinecraftModelPoseProvider;
import net.quepierts.thatskyinteractions.infra.animation.backend.execution.ExecutionState;
import net.quepierts.thatskyinteractions.infra.animation.backend.pipeline.AnimationPipeline;
import net.quepierts.thatskyinteractions.infra.animation.backend.sampler.AnimationSampler;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pass.definition.ParentOverridePassDefinition;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pass.definition.PivotPassDefinition;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline.SkeletonPipeline;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline.SkeletonPoseProvider;
import net.quepierts.thatskyinteractions.infra.animation.backend.source.AnimationSource;
import net.quepierts.thatskyinteractions.infra.animation.core.fsm.FSMParameter;
import net.quepierts.thatskyinteractions.infra.animation.core.fsm.FSMState;
import net.quepierts.thatskyinteractions.infra.animation.core.fsm.FiniteStateMachine;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Slf4j
public final class SequencePlayerAnimation implements PlayerAnimation {

    private final FiniteStateMachine    fsm;
    private final FSMParameter          uniform;

    private final AnimationPipeline     apl;
    private final SkeletonPipeline      spl;

    private final AnimationSampler      enter;
    private final AnimationSampler      main;
    private final AnimationSampler      exit;

    private final AnimationSampler[]    ordinal;

    public SequencePlayerAnimation(
            final FiniteStateMachine    fsm,
            final AnimationPipeline     apl,
            final SkeletonPipeline      spl,
            final AnimationSource       enter,
            final AnimationSource       main,
            final AnimationSource       exit
    ) {
        final var parameter = fsm.uniform();

        this.fsm        = fsm;
        this.uniform    = parameter;

        this.apl        = apl;
        this.spl        = spl;
        this.enter      = enter != null ? enter.link(apl) : null;
        this.main       = main != null ? main.link(apl) : null;
        this.exit       = exit != null ? exit.link(apl) : null;

        parameter.duration()[0] = enter.getDuration();
        parameter.duration()[1] = main.getDuration();
        parameter.duration()[2] = exit.getDuration();

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

        return new SequencePlayerAnimation(
                DefaultMinecraftFSM.SEQUENCE,
                DefaultMinecraftAnimationPipeline.HUMANOID_TIMELINE,
                DefaultMinecraftSkeletonPipeline.MODIFIED_HUMANOID,
                enter,
                main,
                exit
        );
    }

    private static AnimationSource parse(
            final @Nullable SourceDefinition definition,
            final @NonNull BedrockAnimationManager manager
    ) {
        if (definition == null) {
            return null;
        }

        final var identifier    = Identifier.parse(definition.source());
        final var animation     = manager.getAnimation(identifier);

        if (animation == null) {
            log.warn("Animation source not found: {}", identifier);
            return null;
        }

        return BedrockAnimationCompiler.compile(animation);
    }

    @Override
    public void play(
            @NonNull final FSMState state
    ) {
        state.setUniform(this.uniform);
        this.fsm.start(state);
    }

    @Override
    public void update(
            @NonNull final FSMState state,
            final float delta
    ) {
        state.setUniform(this.uniform);
        this.fsm.update(state, delta);
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

//        animation.getExecutionState().copyFrom(executionState);

        animation.bindSource(1, this.ordinal[fsmState.getCurrentState()]);
        animation.submit(animationState, skeleton.getAdapter());

        skeleton.bindUbo(PivotPassDefinition.REQUIRED_UBO, animationState.getUboPivotModification().getBuffer());
        skeleton.bindUbo(ParentOverridePassDefinition.REQUIRED_UBO, animationState.getUboParentOverride().getBuffer());
        skeleton.bindUbo(MinecraftModelPoseProvider.REQUIRED_UBO, animationState.getUboModelOverride().getBuffer());
        skeleton.bindProvider(0, provider);
        skeleton.bindTarget("Output", animationState.getCache());
        skeleton.submit(animationState.getSkeleton());
    }

    @Override
    public void cleanup(@NonNull final FSMState state) {
        this.fsm.reset(state);
    }

}
