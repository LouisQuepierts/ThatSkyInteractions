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
import net.quepierts.thatskyinteractions.feature.registry.AnimationLayerTypes;
import net.quepierts.veynir.backend.execution.ExecutionState;
import net.quepierts.veynir.core.adapter.TransformF;
import net.quepierts.veynir.core.skeleton.PoseCache;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftSkeletonLayout;
import net.quepierts.thatskyinteractions.feature.animation.event.PlayerAnimationControllerEvent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
public final class PlayerAnimationController {

    private final Map<AnimationLayerType, AnimationLayer>   layers;
    private final List<AnimationLayer>                      running;
    private final List<AnimationLayer>                      finished;
    private final @Nullable AnimationLayer @NonNull[]       apply;

    private final ExecutionState                            executionState      = new ExecutionState(64);
    private final TransformF                                root;

    @Getter private final PlayerMask                        executionMask       = PlayerMask.all();
    private final PlayerMask                                exclusionMask       = PlayerMask.empty();

    @Getter private int                                     last;
    @Getter private boolean                                 playing;
    @Getter private boolean                                 ticked              = false;
    @Getter private boolean                                 resolved            = false;
    @Getter private boolean                                 paused              = false;

    public PlayerAnimationController() {
        this.layers     = new Object2ObjectOpenHashMap<>();
        this.running    = new ArrayList<>();
        this.finished   = new ArrayList<>();
        this.apply      = new AnimationLayer[DefaultMinecraftSkeletonLayout.HUMANOID.size()];

        // setup fallback
        this.root       = new TransformF();
        this.root       .setScale(1, 1, 1);
        this.root       .setQuaternion(0, 0, 0, 1);
    }

    public void play(Identifier identifier) {
        this.play(
                identifier,
                AnimationLayerTypes.DEFAULT.get()
        );
    }

    public boolean play(
            final @NonNull Identifier           animationId,
            final @NonNull AnimationLayerType   type
    ) {

        final var layer = this.getLayer(type);

        if (layer.isPlaying()) {
            return false;
        }

        if (type.isExclusive()) {
            if (this.exclusionMask.collision(type.getMask())) {
                return false;
            }
            this.exclusionMask.or(type.getMask());
        }

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

        layer.play(
                animationId,
                animation,
                definition,
                this.executionMask
        );

        this.running.add(layer);
        this.running.sort(AnimationLayer::compareTo);
        this.setupApplyArray();

        this.playing            = true;

        NeoForge.EVENT_BUS.post(new PlayerAnimationControllerEvent.Play.Post(
                this,
                animationId
        ));

        return true;
    }

    public void pause() {
        if (this.playing) {
            this.paused = true;
        }
    }

    public void pause(
            final @Nullable AnimationLayerType   type
    ) {

        if (type == null) {
            this.pause();
            return;
        }

        if (!this.isPlaying()) {
            return;
        }

        final var layer = this.layers.get(type);
        if (layer != null && layer.isPlaying()) {
            layer.pause();
        }
    }

    public void resume() {
        if (this.isPlaying()) {
            this.paused = false;
        }
    }

    public void resume(
            final @Nullable AnimationLayerType   type
    ) {

        if (type == null) {
            this.resume();
            return;
        }

        if (!this.isPlaying()) {
            return;
        }

        final var layer = this.layers.get(type);
        if (layer != null && layer.isPlaying()) {
            layer.resume();
        }
    }

    public void abort() {
        if (!this.isPlaying()) {
            return;
        }

        this.cleanup();
    }

    public void abort(
            final @Nullable AnimationLayerType   type
    ) {

        if (type == null) {
            this.abort();
            return;
        }

        if (!this.isPlaying()) {
            return;
        }

        final var layer = this.layers.get(type);
        if (layer != null && layer.isPlaying()) {
            layer.abort();
        }
    }

    public void exit() {
        if (!this.isPlaying()) {
            return;
        }

        for (final var layer : this.running) {
            layer.exit();
        }
    }

    public void exit(
            final @Nullable AnimationLayerType   type
    ) {

        if (type == null) {
            this.exit();
            return;
        }

        if (!this.isPlaying()) {
            return;
        }

        final var layer = this.layers.get(type);
        if (layer != null && layer.isPlaying()) {
            layer.exit();
        }
    }

    public void tick(int current) {
        if (this.playing && !this.paused) {
            this.ticked = current != last;

            if (this.ticked) {

                for (final var layer : this.running) {
                    layer.tick(current);

                    if (layer.isFinished()) {
                        this.finished.add(layer);
                    }
                }

                if (!this.finished.isEmpty()) {
                    for (final var layer : this.finished) {
                        this.remove(layer);
                    }

                    this.finished.clear();
                    this.setupApplyArray();

                    if (this.running.isEmpty()) {
                        this.cleanup();
                    }
                }

            }
        }

        this.last       = current;
    }

    public void update(float partialTicks) {
        if (this.playing && !this.paused) {
            this.resolved   = false;

            for (final var layer : this.running) {
                layer.update(partialTicks);
            }
        }
    }

    public void resolve(
            final @NonNull MinecraftModelPoseProvider provider
    ) {

        this.markResolved();

        for (final var layer : this.running) {
            if (layer.isTicked() && !layer.isResolved()) {
                layer.resolve(provider);
            }
        }
    }

    public void apply(
            final @NonNull MinecraftModelAdaptor adaptor
    ) {

        if (this.running.isEmpty()) {
            return;
        }

        final var skeleton  = adaptor.getSkeleton();
        for (final var entry : skeleton.getEntries()) {
            final var loc   = entry.id();
            final var layer = this.apply[loc];

            if (layer == null || !layer.isResolved()) {
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


    public boolean isUnlocked(
            final @NonNull PlayerBone bone
    ) {
        for (final var layer : this.running) {
            if (!layer.isPlaying()) {
                continue;
            }

            if (!layer.containsBone(bone)) {
                continue;
            }

            return layer.getDefinition().unlock().contains(bone);
        }
        return false;
    }

    public boolean shouldRestrictMotion() {
        for (final var layer : this.running) {
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
        for (final var layer : this.running) {
            layer.event(event);
        }
    }

    public void event(final int signal) {
        for (final var layer : this.running) {
            layer.event(signal);
        }
    }

    private void remove(
            final @NonNull AnimationLayer layer
    ) {
        this.running.remove(layer);

        final var type = layer.getType();
        if (type.isExclusive()) {
            // because layer.mask is subset from exclusion
            // so we can use xor
            this.exclusionMask.xor(type.getMask());
        }
    }

    private void setupApplyArray() {
        Arrays.fill(this.apply, null);

        for (var i = 0; i < this.apply.length; i++) {
            final var bone = PlayerBone.ordinal(i);
            for (final var layer : this.running) {
                if (layer.containsBone(bone)) {
                    this.apply[bone.getMapped()] = layer;
                    break;
                }
            }
        }
    }

    private @NonNull AnimationLayer getLayer(
            final @NonNull AnimationLayerType type
    ) {

        return this.layers.computeIfAbsent(
                type,
                t -> new AnimationLayer(
                        t,
                        this.executionState, // not used
                        HumanoidAnimationState._default(),
                        this.requestCache()
                )
        );

    }

    private void cleanup() {
        this.playing        = false;
        this.paused         = false;

        this.running        .clear();
    }

    private void markResolved() {
        this.resolved = true;
    }

    private @NonNull PoseCache requestCache() {
        return new PoseCache(DefaultMinecraftSkeletonLayout.HUMANOID);
    }
}
