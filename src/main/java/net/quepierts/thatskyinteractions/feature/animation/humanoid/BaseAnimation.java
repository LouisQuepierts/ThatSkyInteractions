package net.quepierts.thatskyinteractions.feature.animation.humanoid;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.core.animation.model.SourceDefinition;
import net.quepierts.thatskyinteractions.feature.animation.bedrock.BedrockAnimationCompiler;
import net.quepierts.thatskyinteractions.feature.animation.bedrock.BedrockAnimationManager;
import net.quepierts.veynir.backend.source.AnimationSource;
import net.quepierts.veynir.core.fsm.FSMParameter;
import net.quepierts.veynir.core.fsm.FSMState;
import net.quepierts.veynir.core.fsm.FiniteStateMachine;
import net.quepierts.thatskyinteractions.feature.animation.bedrock.ChannelProcessor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Slf4j
@Getter
public abstract class BaseAnimation implements PlayerAnimation {

    protected final FiniteStateMachine  fsm;
    protected final FSMParameter        uniform;

    protected BaseAnimation(final FiniteStateMachine fsm) {
        this.fsm        = fsm;
        this.uniform    = fsm.uniform();
    }

    @Override
    public void play(
            final @NonNull FSMState state
    ) {
        state.setUniform(this.uniform);
        this.fsm.start(state);
    }

    @Override
    public void update(
            final @NonNull FSMState state,
            final float delta
    ) {
        state.setUniform(this.uniform);
        this.fsm.update(state, delta);
    }

    @Override
    public void cleanup(final @NonNull FSMState state) {
        this.fsm.reset(state);
    }

    @Override
    public void event(
            final @NonNull FSMState fsmState,
            final int               event
    ) {
        this.fsm.event(fsmState, event);
    }

    @Override
    public void abort(final @NonNull FSMState fsmState) {
        this.fsm.abort(fsmState);
    }

    @Override
    public void exit(final @NonNull FSMState fsmState) {
        if (this.fsm.getTerminal() == fsmState.getCurrentState()) {
            return;
        }

        this.fsm.exit(fsmState);
    }

    @Override
    public boolean isLooping(final @NonNull FSMState fsmState) {
        return this.fsm.isLooping(fsmState);
    }

    protected static AnimationSource parse(
            final @Nullable SourceDefinition        definition,
            final @NonNull  BedrockAnimationManager manager
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

        final var namespace = definition.namespace();
        return BedrockAnimationCompiler.compile(
                animation,
                namespace.map(
                        s -> ChannelProcessor.namespaced(s, "_")
                ).orElse(ChannelProcessor.none())
        );
    }

    protected static void setParameter(
            final int               index,
            final FSMParameter      parameter,
            final SourceDefinition  definition,
            final AnimationSource   source
    ) {
        if (definition == null ||
                source == null) {
            return;
        }

        parameter.fadeIn()[index]   = definition.fadeIn();
        parameter.fadeOut()[index]  = definition.fadeOut();
        parameter.duration()[index] = source.getDuration();
    }

}
