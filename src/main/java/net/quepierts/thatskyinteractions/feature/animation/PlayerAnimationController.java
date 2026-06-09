package net.quepierts.thatskyinteractions.feature.animation;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.NeoForge;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerBone;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerMask;
import net.quepierts.thatskyinteractions.feature.client.model.MinecraftModelAdaptor;
import net.quepierts.thatskyinteractions.feature.client.model.MinecraftModelPoseProvider;
import net.quepierts.veynir.backend.execution.ExecutionState;
import net.quepierts.veynir.core.adapter.TransformF;
import net.quepierts.veynir.core.skeleton.PoseCache;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftSkeletonLayout;
import net.quepierts.thatskyinteractions.feature.animation.event.PlayerAnimationControllerEvent;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.Map;

@Slf4j
public final class PlayerAnimationController {

    private final Map<AnimationLayerType, AnimationLayer>   layers;
    private final AnimationLayer[]                          apply;

    private final ExecutionState                            executionState      = new ExecutionState(64);
    private final PlayerMask                                exclusionMask       = PlayerMask.empty();

    private final TransformF                                root;

    @Getter private int                                     last;
    @Getter private boolean                                 playing;
    @Getter private boolean                                 ticked              = false;
    @Getter private boolean                                 resolved            = false;
    @Getter private boolean                                 paused              = false;

    public PlayerAnimationController() {
        this.layers = new Object2ObjectOpenHashMap<>();
        this.apply  = new AnimationLayer[DefaultMinecraftSkeletonLayout.HUMANOID.size()];
        this.root   = new TransformF();
        this.root   .setScale(1, 1, 1);
    }

    public void play(Identifier identifier) {
        this.play(
                identifier,
                AnimationLayerType.MAIN
        );
    }

    public boolean play(
            final @NonNull Identifier           animationId,
            final @NonNull AnimationLayerType   type
    ) {

        // todo: multilayer not supported yet
        if (!this.layers.isEmpty()) {
            return false;
        }

        /*if (this.layers.containsKey(type)) {
            return false;
        }

        if (type.exclusive()) {
            if (this.exclusionMask.collision(type.defaultMask())) {
                return false;
            }
            this.exclusionMask.or(type.defaultMask());
        }*/

        final var manager       = PlayerAnimationManager.getInstance();
        final var animation     = manager.get(animationId);
        final var definition    = manager.getDefinition(animationId);

        final var pre           = NeoForge.EVENT_BUS.post(new PlayerAnimationControllerEvent.Play.Pre(
                this,
                animationId
        ));

        if (pre.isCanceled()) {
            return false;
        }

        if (animation == null) {
            log.warn("Animation source not found: {}", animationId);
            return false;
        }

        final var layer = new AnimationLayer(
                AnimationLayerType.MAIN,
                animationId,
                animation,
                definition,
                this.executionState, // not used
                HumanoidAnimationState._default(),
                this.requestCache()
        );
        layer            .play();

        this.layers             .put(type, layer);
        Arrays.fill(this.apply, layer); // todo: masked apply

        this.playing            = true;

        NeoForge.EVENT_BUS.post(new PlayerAnimationControllerEvent.Play.Post(
                this,
                animationId
        ));

        return true;
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

        for (final var layer : this.layers.values()) {
            layer.exit();
        }
    }

    public void tick(int current) {
        if (this.playing && !this.paused) {
            this.ticked             = current != last;

            for (final var layer : this.layers.values()) {
                layer.tick(current);

                if (layer.isFinished()) {
                    this.cleanup(layer);
                }
            }

//            final var lastElapsed   = this.fsmState.getElapsed();
//            this.current            .tick(current);
            /*final var elapsed       = this.fsmState.getElapsed();
            this.state.progress     = elapsed;

            if (lastElapsed > elapsed) { // state may change
                NeoForge.EVENT_BUS.post(new PlayerAnimationControllerEvent.StateChanged(
                        this,
                        this.fsmState.getLastState(),
                        this.fsmState.getCurrentState()
                ));
            }*/

            /*if (this.current.isFinished()) {
                this.cleanup();
                NeoForge.EVENT_BUS.post(new PlayerAnimationControllerEvent.Finished(this));
            }*/
        }

        this.last       = current;
    }

    public void update(float partialTicks) {
        if (this.playing && !this.paused) {
            this.resolved   = false;

            for (final var layer : this.layers.values()) {
                layer.update(partialTicks);
            }

//            this.current    .update(partialTicks);
//            this.alpha      = this.current.getAlpha();
            /*final var delta = partialTicks * 0.05f;
            this.state.progress         = this.fsmState.getElapsed() + delta;
            var progress                = Math.min(
                    (this.fsmState.getBlendElapsed() + delta)
                            / this.fsmState.getBlendDuration(),
                    1.0f
            );
            this.alpha                  = getAlpha(this.fsmState, progress);*/
        }
    }

    public void resolve(
            final @NonNull MinecraftModelPoseProvider provider
    ) {

        this.markResolved();

        for (final var layer : this.layers.values()) {
            if (layer.isTicked() && !layer.isResolved()) {
                layer.resolve(provider);
            }
        }

        /*current.getAnimation().resolve(
                current.getFsmState(),
                current.getExecutionState(),
                current.getState(),
                current.getCache(),
                adaptor.link(DefaultMinecraftSkeletonPipeline.MODIFIED_HUMANOID));
        controller.markResolved();*/
    }

    public void apply(
            final @NonNull MinecraftModelAdaptor adaptor
    ) {

        if (this.layers.isEmpty()) {
            return;
        }

        final var skeleton  = adaptor.getSkeleton();
        for (final var entry : skeleton.getEntries()) {
            final var loc   = entry.id();
            final var layer = this.apply[loc];

            if (layer == null) {
                continue;
            }

            final var view  = layer.getCache().get(loc);
            final var alpha = layer.getAlpha();

            if (alpha == 1.0f) {
                entry.setPosition(view.getTx(), view.getTy(), view.getTz());
                entry.setQuaternion(view.getRx(), view.getRy(), view.getRz(), view.getRw());
                entry.setScale(view.getSx(), view.getSy(), view.getSz());
            } else {
                entry.setPosition(view.getTx(), view.getTy(), view.getTz(), alpha);
                entry.setQuaternion(view.getRx(), view.getRy(), view.getRz(), view.getRw(), alpha);
                entry.setScale(view.getSx(), view.getSy(), view.getSz(), alpha);
            }
        }

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


    public boolean isUnlocked(
            final @NonNull PlayerBone bone
    ) {
        for (final var layer : this.layers.values()) {
            if (!layer.containsBone(bone)) {
                continue;
            }

            return layer.getDefinition().unlock().contains(bone);
        }
        return false;
    }

    public boolean shouldRestrictMotion() {
        for (final var layer : this.layers.values()) {
            if (layer.getDefinition().restrictMotion()) {
                return true;
            }
        }
        return false;
    }

    public TransformF getRootTransform() {
        final var layer = this.apply[0];
        return layer != null ? layer.getCache().get(0) : this.root;
    }

    public float getRootAlpha() {
        final var layer = this.apply[0];
        return layer != null ? layer.getAlpha() : 0.0f;
    }

    public void event(final String event) {
        for (final var layer : this.layers.values()) {
            layer.event(event);
        }
    }

    public void event(final int signal) {
        for (final var layer : this.layers.values()) {
            layer.event(signal);
        }
    }

    private void cleanup() {
        this.playing        = false;
        this.paused         = false;

        this.layers         .clear();
        this.exclusionMask  .clear();
    }

    private void cleanup(
            final @NonNull AnimationLayer layer
    ) {
        for (var i = 0; i < this.apply.length; i++) {
            if (this.apply[i] == layer) {
                this.apply[i] = null;
            }
        }
    }

    private void markResolved() {
        this.resolved = true;
    }

    private @NonNull PoseCache requestCache() {
        return new PoseCache(DefaultMinecraftSkeletonLayout.HUMANOID);
    }
}
