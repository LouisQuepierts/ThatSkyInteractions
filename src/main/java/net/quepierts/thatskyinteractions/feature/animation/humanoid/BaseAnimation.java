package net.quepierts.thatskyinteractions.feature.animation.humanoid;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.core.animation.model.SourceDefinition;
import net.quepierts.thatskyinteractions.feature.animation.bedrock.BedrockAnimationCompiler;
import net.quepierts.thatskyinteractions.feature.animation.bedrock.BedrockAnimationManager;
import net.quepierts.thatskyinteractions.infra.animation.backend.source.AnimationSource;
import net.quepierts.thatskyinteractions.infra.animation.core.fsm.FSMParameter;
import net.quepierts.thatskyinteractions.infra.animation.core.fsm.FSMState;
import net.quepierts.thatskyinteractions.infra.animation.core.fsm.FiniteStateMachine;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Slf4j
public abstract class BaseAnimation implements PlayerAnimation {

    @Getter
    protected final FiniteStateMachine  fsm;
    protected final FSMParameter        uniform;

    protected BaseAnimation(final FiniteStateMachine fsm) {
        this.fsm        = fsm;
        this.uniform    = fsm.uniform();
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
    public void cleanup(@NonNull final FSMState state) {
        this.fsm.reset(state);
    }

    protected static AnimationSource parse(
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
