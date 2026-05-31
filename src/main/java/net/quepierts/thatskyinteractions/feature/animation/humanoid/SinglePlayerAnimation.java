package net.quepierts.thatskyinteractions.feature.animation.humanoid;

import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerAnimationDefinition;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftAnimationPipeline;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftFSM;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftSkeletonPipeline;
import net.quepierts.thatskyinteractions.feature.animation.HumanoidAnimationState;
import net.quepierts.thatskyinteractions.feature.client.model.MinecraftModelPoseProvider;
import net.quepierts.thatskyinteractions.feature.data.DataSyncSystem;
import net.quepierts.animata4j.backend.execution.ExecutionState;
import net.quepierts.animata4j.backend.pipeline.AnimationPipeline;
import net.quepierts.animata4j.backend.sampler.AnimationSampler;
import net.quepierts.animata4j.backend.sampler.SamplingMode;
import net.quepierts.animata4j.backend.skeleton.pass.definition.ParentOverridePassDefinition;
import net.quepierts.animata4j.backend.skeleton.pass.definition.PivotPassDefinition;
import net.quepierts.animata4j.backend.skeleton.pipeline.SkeletonPipeline;
import net.quepierts.animata4j.backend.skeleton.pipeline.SkeletonPoseProvider;
import net.quepierts.animata4j.backend.source.AnimationSource;
import net.quepierts.animata4j.core.fsm.FSMState;
import net.quepierts.animata4j.core.model.ParentOverrideConfiguration;
import org.jspecify.annotations.NonNull;

public final class SinglePlayerAnimation extends BaseAnimation {

    private final AnimationPipeline             apl;
    private final SkeletonPipeline              spl;
    private final AnimationSampler              sampler;

    private final ParentOverrideConfiguration   override;

    public SinglePlayerAnimation(
            final AnimationPipeline             apl,
            final SkeletonPipeline              spl,
            final AnimationSource               source,
            final ParentOverrideConfiguration   override
            ) {
        super(DefaultMinecraftFSM.SINGLE);
        this.apl        = apl;
        this.spl        = spl;
        this.sampler    = source.link(apl);
        this.override   = override;
    }

    public static PlayerAnimation parse(final @NonNull PlayerAnimationDefinition definition) {
        final var manager   = DataSyncSystem.BEDROCK_ANIMATION;
        final var overrides = DataSyncSystem.PARENT_OVERRIDE;

        final var sources   = definition.sources();
        final var source    = sources.get("main");
        final var main      = parse(source, manager);

        final var override  = overrides.get(
                Identifier.parse(definition.override()),
                DefaultMinecraftSkeletonPipeline.MODIFIED_HUMANOID.getLayout()
        );

        final var animation = new SinglePlayerAnimation(
                DefaultMinecraftAnimationPipeline.HUMANOID_TIMELINE,
                DefaultMinecraftSkeletonPipeline.MODIFIED_HUMANOID,
                main,
                override
        );

        setParameter(1, animation.uniform, source, main);

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

//        animation.getExecutionState().copyFrom(executionState);

        final var transition    = fsmState.isBlending();
        final var state         = fsmState.getCurrentState();

        final var mode          = state == 0 ? SamplingMode.FREEZE_START : (transition ?
                                SamplingMode.byId(state) :
                                SamplingMode.DEFAULT);

        animation.setSamplingMode(1, mode);
        animation.bindSource(1, this.sampler);
        animation.submit(animationState, skeleton.getAdapter());

        final var parentOverride = animationState.getUboParentOverride();
        parentOverride.getParameter().upload(
                this.override,
                parentOverride.getBuffer()
        );

        skeleton.bindUbo(PivotPassDefinition.REQUIRED_UBO, animationState.getUboPivotModification().getBuffer());
        skeleton.bindUbo(ParentOverridePassDefinition.REQUIRED_UBO, parentOverride.getBuffer());
        skeleton.bindUbo(MinecraftModelPoseProvider.REQUIRED_UBO, animationState.getUboModelOverride().getBuffer());
        skeleton.bindProvider(0, provider);
        skeleton.bindTarget("Output", animationState.getCache());
        skeleton.submit(animationState.getSkeleton());
    }

}
