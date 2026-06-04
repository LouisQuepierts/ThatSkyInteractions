package net.quepierts.thatskyinteractions.feature.animation.humanoid;

import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.quepierts.veynir.core.skeleton.PoseCache;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftAnimationPipeline;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftFSM;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftSkeletonPipeline;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerAnimationDefinition;
import net.quepierts.thatskyinteractions.feature.animation.HumanoidAnimationState;
import net.quepierts.thatskyinteractions.feature.animation.ParentOverrideManager;
import net.quepierts.thatskyinteractions.feature.animation.bedrock.BedrockAnimationManager;
import net.quepierts.thatskyinteractions.feature.client.model.MinecraftModelPoseProvider;
import net.quepierts.veynir.backend.execution.ExecutionState;
import net.quepierts.veynir.backend.pipeline.AnimationPipeline;
import net.quepierts.veynir.backend.sampler.AnimationSampler;
import net.quepierts.veynir.backend.sampler.SamplingMode;
import net.quepierts.veynir.backend.skeleton.pass.definition.ParentOverridePassDefinition;
import net.quepierts.veynir.backend.skeleton.pass.definition.PivotPassDefinition;
import net.quepierts.veynir.backend.skeleton.pipeline.SkeletonPipeline;
import net.quepierts.veynir.backend.skeleton.pipeline.SkeletonPoseProvider;
import net.quepierts.veynir.core.fsm.FSMState;
import net.quepierts.veynir.core.fsm.FiniteStateMachine;
import net.quepierts.veynir.core.model.ParentOverrideConfiguration;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Arrays;

public class TemplateAnimation extends BaseAnimation {

    protected final AnimationPipeline               apl;
    protected final SkeletonPipeline                spl;
    protected final ParentOverrideConfiguration     override;

    protected final AnimationSampler[]              samplers;

    protected final int                             sysEnter;
    protected final int                             sysExit;
    protected final boolean[]                       frozenEnds;
    protected final int[]                           exitPoints;

    public TemplateAnimation(
            final FiniteStateMachine                fsm,
            final AnimationPipeline                 apl,
            final SkeletonPipeline                  spl,
            final ParentOverrideConfiguration       override,
            final AnimationSampler[]                samplers
    ) {
        super(fsm);

        final var lookup    = fsm.getLookup();
        this.sysEnter       = lookup.find("system#enter");
        this.sysExit        = lookup.find("system#exit");
        this.frozenEnds     = new boolean[lookup.size()];
        this.exitPoints     = new int[lookup.size()];

        Arrays.fill(this.exitPoints, this.sysExit);

        this.samplers       = samplers;

        this.apl            = apl;
        this.spl            = spl;
        this.override       = override;
    }

    @Override
    public void resolve(
            @NonNull final FSMState                 fsmState,
            @NonNull final ExecutionState           executionState,
            @NonNull final HumanoidAnimationState   animationState,
            @NonNull final PoseCache                target,
            @NonNull final SkeletonPoseProvider     provider
    ) {
        final var animation = this.apl;
        final var skeleton  = this.spl;

        final var mode      = this.resolveSamplingMode(fsmState);
        final var sampler   = this.resolveSampler(fsmState);

        animation.setSamplingMode(1, mode);
        animation.bindSource(1, sampler);
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
        skeleton.bindTarget("Output", target);
        skeleton.submit(animationState.getSkeleton());
    }

    @Override
    public void exit(final @NonNull FSMState fsmState) {
        final var state     = fsmState.getCurrentState();
        final var exitPoint = this.exitPoints[state];
        if (exitPoint != state) {
            this.fsm.event(fsmState, exitPoint);
        }
    }

    public void setFrozenEnd(
            final int       state,
            final boolean   frozen
    ) {
        this.frozenEnds[state] = frozen;
    }

    public void setExitPoint(
            final int   state,
            final int   point
    ) {
        this.exitPoints[state] = point;
    }

    public boolean hasSource(final String state) {
        final var lookup    = this.fsm.getLookup();
        final var id        = lookup.find(state);

        return id != -1 && this.samplers[id - 1] != null;
    }

    protected AnimationSampler resolveSampler(FSMState fsmState) {
        final var currentState  = fsmState.getCurrentState();
        final var lastState     = fsmState.getLastState();
        final var clamped       = Mth.clamp(
                (lastState != -1 && this.frozenEnds[lastState] ? lastState : currentState) - 1,
                0, this.samplers.length - 1
        );
        return this.samplers[clamped];
    }

    protected SamplingMode resolveSamplingMode(FSMState fsmState) {

        final var transition    = fsmState.isBlending();
        final var currentState  = fsmState.getCurrentState();
        final var lastState     = fsmState.getLastState();

        if (currentState == this.sysEnter ||
            lastState == this.sysEnter && transition) {
            return SamplingMode.FREEZE_START;
        }

        if (currentState == this.sysExit ||
            transition && lastState != -1 && this.frozenEnds[lastState]) {
            return SamplingMode.FREEZE_END;
        }

        return SamplingMode.DEFAULT;

    }

    public static TemplateAnimation template(
            final @NonNull PlayerAnimationDefinition    definition,
            final @NonNull FiniteStateMachine           fsm,
            final @NonNull AnimationPipeline            apl,
            final @NonNull SkeletonPipeline             spl
    ) {
        final var lookup    = fsm.getLookup();
        final var builder   = new ArrayList<String>(lookup.size());
        for (final var string : lookup) {
            if (!string.startsWith("system#")) {
                builder.add(string);
            }
        }

        final var names    = builder.toArray(String[]::new);

        return TemplateAnimation.template(
                definition,
                fsm,
                apl,
                spl,
                names
        );
    }

    public static TemplateAnimation template(
            final @NonNull PlayerAnimationDefinition    definition,
            final @NonNull FiniteStateMachine           fsm,
            final @NonNull AnimationPipeline            apl,
            final @NonNull SkeletonPipeline             spl,
            final @NonNull String...                    names
    ) {
        final var manager   = BedrockAnimationManager.getInstance();
        final var overrides = ParentOverrideManager.getInstance();

        final var lookup    = fsm.getLookup();

        final var size      = names.length;

        final var sources   = definition.sources();
        final var samplers  = new AnimationSampler[size];

        final var override  = overrides.get(
                Identifier.parse(definition.override()),
                spl.getLayout()
        );

        final var animation = new TemplateAnimation(
                fsm,
                apl, spl,
                override,
                samplers
        );

        for (final String name : names) {
            if (name.startsWith("system#")) {
                continue;
            }

            final var def       = sources.get(name);
            final var source    = parse(def, manager);

            if (source == null) {
                continue;
            }

            final var id        = lookup.find(name);
            if (id == -1) {
                continue;
            }

            samplers[id - 1]    = source.link(apl);

            setParameter(
                    id,
                    animation.uniform,
                    def,
                    source
            );
        }

        return animation;
    }

    public static TemplateAnimation single(final @NonNull PlayerAnimationDefinition definition) {
        return template(
                definition,
                DefaultMinecraftFSM.SINGLE,
                DefaultMinecraftAnimationPipeline.HUMANOID_TIMELINE,
                DefaultMinecraftSkeletonPipeline.MODIFIED_HUMANOID
        );
    }

    public static TemplateAnimation sequence(final @NonNull PlayerAnimationDefinition definition) {

        final var animation = template(
                definition,
                DefaultMinecraftFSM.SEQUENCE,
                DefaultMinecraftAnimationPipeline.HUMANOID_TIMELINE,
                DefaultMinecraftSkeletonPipeline.MODIFIED_HUMANOID
        );

        animation.frozenEnds[3] = true;

        return animation;
    }
}
