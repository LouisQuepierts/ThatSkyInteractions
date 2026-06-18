package net.quepierts.thatskyinteractions.feature.animation;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftChannelFormat;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftSkeletonLayout;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerAnimationDefinition;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerBone;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerMask;
import net.quepierts.thatskyinteractions.feature.animation.humanoid.PlayerAnimation;
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

    private final PlayerMask                mask;
    private final boolean[]                 skeletonMask;

    private final int                       priority;

    private Identifier                      animationId;
    private PlayerAnimation                 animation;
    private PlayerAnimationDefinition       definition;
    private float                           speed = 1.0f;

    private float                           alpha;
    private boolean                         playing;
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

        this.mask                           = PlayerMask.empty();
        this.skeletonMask                   = new boolean[DefaultMinecraftSkeletonLayout.HUMANOID.size()];
        this.fsmState                       = new FSMState();

        this.state                          .getSkeleton()
                                            .setMask(this.skeletonMask);
    }

    public void play(
            Identifier                      animationId,
            PlayerAnimation                 animation,
            PlayerAnimationDefinition       definition,
            PlayerMask                      mask
    ) {
        this.animationId                    = animationId;
        this.animation                      = animation;
        this.definition                     = definition;

        this.setupMask(mask);
        this.play();
    }

    public void play() {
        this.playing                        = true;
        this.paused                         = false;
        this.alpha                          = 0.0f;
        this.state.progress                 = 0.0f;

        this.animation                      .play(this.fsmState);
    }

    public void tick(float pDelta) {
        if (!this.playing || this.paused) {
            return;
        }

        final var delta = pDelta * this.speed;

        this.animation.update(this.fsmState, delta);
        this.state.progress = this.fsmState.getElapsed();

        if (this.fsmState.isFinished()) {
            this.cleanup();
        }
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
            final @NonNull AnimationResolveContext context
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
                context
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
        return this.mask.contains(bone);
    }

    public boolean isFinished() {
        return !this.playing || this.fsmState.isFinished();
    }

    private void setupMask(
            final @NonNull PlayerMask execution
    ) {
        this.type.getMask().copy(this.mask);
        this.mask.and(execution);

        final var attribute = this.state.getChannelAttribute();
        final var size      = DefaultMinecraftChannelFormat.ATTRIBUTE_SIZE;
        final var offset    = DefaultMinecraftChannelFormat.OFFSET_MASK;

        final var iterator = PlayerBone.iterator();
        while (iterator.hasNext()) {
            final var bone              = iterator.next();
            final var mapped            = bone.getMapped();

            if (mapped == -1) {
                continue;
            }

            final var enable            = this.mask.contains(bone);
            this.skeletonMask[mapped]   = enable;

            // set attribute for bone. position/rotation/scale
            final var location          = mapped * 3;
            attribute.write(size * location + offset, enable);
            attribute.write(size * (location + 1) + offset, enable);
            attribute.write(size * (location + 2) + offset, enable);
        }

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
    public int compareTo(final @NonNull AnimationLayer other) {
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

    public Serialize serialize() {
        return new Serialize(
                this.type.getIdentifier(),
                this.animationId,
                this.alpha,
                this.speed,

                this.playing,
                this.paused,

                this.mask.copy(),
                this.fsmState.duplicate()
        );
    }

    public void deserialize(final @NonNull Serialize serialize) {

        if (!serialize.playing) {
            this.cleanup();
            return;
        }

        if (!serialize.animation.equals(this.animationId)) {
            final var manager       = PlayerAnimationManager.getInstance();
            final var animation     = manager.get(serialize.animation);
            final var definition    = manager.getDefinition(serialize.animation);

            this.play(
                    serialize.animation,
                    animation,
                    definition,
                    serialize.mask
            );
        }

        this.alpha                  = serialize.alpha;
        this.speed                  = serialize.speed;
        this.paused                 = serialize.paused;

        this.fsmState               .copyData(serialize.fsmState);

    }

    public record Serialize(
            Identifier          type,
            Identifier          animation,
            float               alpha,
            float               speed,

            boolean             playing,
            boolean             paused,


            PlayerMask          mask,
            FSMState            fsmState
    ) { }

    public static final StreamCodec<ByteBuf, PlayerMask> MASK_STREAM_CODEC
            = ByteBufCodecs.INT.map(PlayerMask::direct, PlayerMask::getMask);

    public static final StreamCodec<ByteBuf, FSMState> FSM_STREAM_CODEC
            = StreamCodec.composite(
                    ByteBufCodecs.FLOAT,
                    FSMState::getElapsed,
                    ByteBufCodecs.FLOAT,
                    FSMState::getBlendElapsed,
                    ByteBufCodecs.FLOAT,
                    FSMState::getBlendDuration,
                    ByteBufCodecs.FLOAT,
                    FSMState::getNormalizedElapsed,
                    ByteBufCodecs.FLOAT,
                    FSMState::getNormalizedBlendElapsed,
                    ByteBufCodecs.INT,
                    FSMState::getLastState,
                    ByteBufCodecs.INT,
                    FSMState::getCurrentState,
                    ByteBufCodecs.BOOL,
                    FSMState::isFinished,
                    ByteBufCodecs.BOOL,
                    FSMState::isBlending,
                    FSMState::direct
            );

    public static final StreamCodec<ByteBuf, Serialize> STREAM_CODEC
            = StreamCodec.composite(
                    Identifier.STREAM_CODEC,
                    Serialize::type,
                    Identifier.STREAM_CODEC,
                    Serialize::animation,
                    ByteBufCodecs.FLOAT,
                    Serialize::alpha,
                    ByteBufCodecs.FLOAT,
                    Serialize::speed,
                    ByteBufCodecs.BOOL,
                    Serialize::playing,
                    ByteBufCodecs.BOOL,
                    Serialize::paused,
                    MASK_STREAM_CODEC,
                    Serialize::mask,
                    FSM_STREAM_CODEC,
                    Serialize::fsmState,
                    Serialize::new
            );
}
