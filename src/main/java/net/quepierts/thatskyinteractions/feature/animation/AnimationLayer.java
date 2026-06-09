package net.quepierts.thatskyinteractions.feature.animation;

import lombok.Getter;
import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerAnimationDefinition;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerBone;
import net.quepierts.thatskyinteractions.feature.animation.humanoid.PlayerAnimation;
import net.quepierts.thatskyinteractions.feature.client.model.MinecraftModelPoseProvider;
import net.quepierts.veynir.backend.execution.ExecutionState;
import net.quepierts.veynir.core.fsm.FSMState;
import net.quepierts.veynir.core.skeleton.PoseCache;
import org.jspecify.annotations.NonNull;

@Getter
public final class AnimationLayer implements Comparable<AnimationLayer> {

    private final AnimationLayerType        type;

    private final ExecutionState            executionState;
    private final HumanoidAnimationState    state;
    private final FSMState                  fsmState;
    private final PoseCache                 cache;

    private final int                       priority;

    private Identifier                      animationId;
    private PlayerAnimation                 animation;
    private PlayerAnimationDefinition       definition;

    private float                           alpha;
    private int                             last;
    private boolean                         playing;
    private boolean                         ticked;
    private boolean                         resolved;
    private boolean                         paused;

    public AnimationLayer(
            AnimationLayerType              type,
            ExecutionState                  executionState,
            HumanoidAnimationState          state,
            PoseCache                       cache
    ) {
        this.type                           = type;
        this.priority                       = type.getPriority();
        this.executionState                 = executionState;
        this.state                          = state;
        this.cache                          = cache;
        this.fsmState                       = new FSMState();
    }

    public void play(
            Identifier                      animationId,
            PlayerAnimation                 animation,
            PlayerAnimationDefinition       definition
    ) {
        this.animationId                    = animationId;
        this.animation                      = animation;
        this.definition                     = definition;

        this.play();
    }

    public void play() {
        this.playing                        = true;
        this.paused                         = false;
        this.alpha                          = 0.0f;
        this.state.progress                 = 0.0f;

        this.animation                      .play(this.fsmState);
    }

    public void tick(int current) {
        if (!this.playing || this.paused) {
            return;
        }

        final var delta                     = (current - this.last) * 0.05f;
        this.ticked                         = current != this.last;

        this.animation.update(this.fsmState, delta);
        this.state.progress = this.fsmState.getElapsed();

        if (this.fsmState.isFinished()) {
            this.cleanup();
        }

        this.last = current;
    }

    public void update(float partialTicks) {
        if (!this.playing || this.paused) {
            return;
        }

        this.resolved = false;
        final var delta = partialTicks * 0.05f;
        this.state.progress = this.fsmState.getElapsed() + delta;

        var progress = Math.min(
                (this.fsmState.getBlendElapsed() + delta) / this.fsmState.getBlendDuration(),
                1.0f
        );
        this.alpha = this.computeAlpha(progress);
    }

    public void resolve(
            final @NonNull MinecraftModelPoseProvider provider
    ) {

        if (!this.playing) {
            return;
        }

        this.markResolved();

        this.getAnimation().resolve(
                this.fsmState,
                this.executionState,
                this.state,
                this.cache,
                provider
        );
    }

    public void abort() {
        if (!this.playing) {
            return;
        }
        this.cleanup();
    }

    public void exit() {
        if (!this.playing) {
            return;
        }
        this.animation.exit(this.fsmState);
    }

    public void pause() {
        if (this.playing) {
            this.paused = true;
        }
    }

    public void resume() {
        if (this.playing) {
            this.paused = false;
        }
    }

    public void markResolved() {
        this.resolved = true;
    }

    public boolean containsBone(PlayerBone bone) {
        return this.type.getMask().contains(bone);
    }

    public boolean isFinished() {
        return !this.playing || this.fsmState.isFinished();
    }

    private void cleanup() {
        this.animation      .cleanup(this.fsmState);
        this.playing        = false;
        this.paused         = false;

        this.animationId    = null;
        this.animation      = null;
        this.definition     = null;
    }

    private float computeAlpha(float progress) {
        final var fsm           = this.animation.getFsm();
        final var lookup        = fsm.getLookup();
        final var currentState  = this.fsmState.getCurrentState();
        final var lastState     = this.fsmState.getLastState();

        if (currentState == 0) {
            return 0.0f;
        } else if ("system#exit".equals(lookup.name(currentState))) {
            return 1.0f - progress;
        } else if (lastState != -1 && "system#enter".equals(lookup.name(lastState))) {
            return progress;
        }

        return 1.0f;
    }

    @Override
    public int hashCode() {
        return this.type.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj == this
                || obj.getClass() == AnimationLayer.class && (((AnimationLayer) obj)).type.equals(this.type);
    }

    @Override
    public int compareTo(@NonNull final AnimationLayer other) {
        return Integer.compare(this.priority, other.priority);
    }

    public void event(final String event) {
        final var fsm       = this.animation.getFsm();
        final var signal    = "exit".equals(event) ? -1 : fsm.getLookup().find(event);
        this.event(signal);
    }

    public void event(final int event) {
        this.animation.event(this.fsmState, event);
    }
}
