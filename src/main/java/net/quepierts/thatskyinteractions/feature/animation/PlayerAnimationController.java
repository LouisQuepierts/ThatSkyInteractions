package net.quepierts.thatskyinteractions.feature.animation;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.NeoForge;
import net.quepierts.animata4j.backend.execution.ExecutionState;
import net.quepierts.animata4j.core.fsm.FSMState;
import net.quepierts.animata4j.core.skeleton.PoseCache;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftSkeletonLayout;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerAnimationDefinition;
import net.quepierts.thatskyinteractions.feature.animation.event.PlayerAnimationControllerEvent;
import net.quepierts.thatskyinteractions.feature.animation.humanoid.PlayerAnimation;

@Slf4j
@Getter
public final class PlayerAnimationController {

    private final HumanoidAnimationState state;

    private final ExecutionState        executionState      = new ExecutionState(64);
    private final FSMState              fsmState            = new FSMState();
    private final PoseCache             cache               = new PoseCache(DefaultMinecraftSkeletonLayout.HUMANOID);

    private Identifier                  current;
    private PlayerAnimation             animation;
    private PlayerAnimationDefinition   definition;

    private float   alpha = 0.0f;
    private int     last;
    private boolean playing;
    private boolean ticked = false;
    private boolean resolved = false;

    public PlayerAnimationController() {
        this.state  = HumanoidAnimationState._default();
    }

    public void play(Identifier identifier) {
        final var manager       = PlayerAnimationManager.getInstance();
        final var animation     = manager.get(identifier);
        final var definition    = manager.getDefinition(identifier);

        final var pre           = NeoForge.EVENT_BUS.post(new PlayerAnimationControllerEvent.PrePlay(
                this,
                identifier
        ));

        if (pre.isCanceled()) {
            return;
        }

        if (animation == null) {
            log.warn("Animation source not found: {}", identifier);
            return;
        }

        this.current            = identifier;
        this.animation          = animation;
        this.definition         = definition;

        this.playing            = true;
        this.state.progress     = 0.0f;

        NeoForge.EVENT_BUS.post(new PlayerAnimationControllerEvent.PostPlay(this));
    }

    public void abort() {
        if (!this.isPlaying()) {
            return;
        }

        this.cleanup();
    }

    public void exit() {
        if (!this.isPlaying()) {
            return;
        }

        this.animation.exit(this.fsmState);
    }

    public void tick(int current) {
        if (this.playing) {
            final var delta         = (current - last) * 0.05f;
            this.ticked             = current != last;

            final var lastElapsed   = this.fsmState.getElapsed();
            this.animation          .update(this.fsmState, delta);
            final var elapsed       = this.fsmState.getElapsed();
            this.state.progress     = elapsed;

            if (lastElapsed > elapsed) { // state may change
                NeoForge.EVENT_BUS.post(new PlayerAnimationControllerEvent.StateChanged(
                        this,
                        this.fsmState.getLastState(),
                        this.fsmState.getCurrentState()
                ));
            }

            if (this.fsmState.isFinished()) {
                this.cleanup();
                NeoForge.EVENT_BUS.post(new PlayerAnimationControllerEvent.Finished(this));
            }
        }

        this.last       = current;
    }

    public void update(float partialTicks) {
        if (this.playing) {
            this.resolved   = false;
            final var delta = partialTicks * 0.05f;
            this.state.progress         = this.fsmState.getElapsed() + delta;
            var progress                = Math.min(
                    (this.fsmState.getBlendElapsed() + delta)
                            / this.fsmState.getBlendDuration(),
                    1.0f
            );
            this.alpha                  = getAlpha(this.fsmState, progress);
        }
    }

    private void cleanup() {
        this.animation.cleanup(this.fsmState);
        this.playing    = false;
        this.current    = null;
        this.animation  = null;
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
