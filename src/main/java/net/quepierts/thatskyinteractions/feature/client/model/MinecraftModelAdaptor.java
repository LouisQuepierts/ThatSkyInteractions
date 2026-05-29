package net.quepierts.thatskyinteractions.feature.client.model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.quepierts.animata4j.backend.skeleton.pipeline.*;
import net.quepierts.animata4j.core.skeleton.PoseCache;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public final class MinecraftModelAdaptor {

    @Getter
    private final MinecraftModelSkeleton                            skeleton;
    private final Map<SkeletonPipeline, MinecraftModelPoseProvider> linked;

    @Setter
    @Getter
    private float   alpha   = 1.0f;

    private MinecraftModelAdaptor(final MinecraftModelSkeleton    skeleton) {
        this.skeleton   = skeleton;
        this.linked     = new HashMap<>();
    }

    public static MinecraftModelAdaptor of(@NonNull MinecraftModelSkeleton skeleton) {
        return new MinecraftModelAdaptor(skeleton);
    }

    public MinecraftModelPoseProvider link(@NonNull SkeletonPipeline pipeline) {
        return linked.computeIfAbsent(pipeline, p -> MinecraftModelPoseProvider.of(this.skeleton, p));
    }

    public void accept(
            @NonNull final PoseCache cache
    ) {
        for (final var entry : this.skeleton.getEntries()) {
            final var loc       = entry.id();
            final var view      = cache.get(loc);

            if (this.alpha == 1.0f) {
                entry.setPosition(view.getTx(), view.getTy(), view.getTz());
                entry.setQuaternion(view.getRx(), view.getRy(), view.getRz(), view.getRw());
                entry.setScale(view.getSx(), view.getSy(), view.getSz());
            } else {
                entry.setPosition(view.getTx(), view.getTy(), view.getTz(), this.alpha);
                entry.setQuaternion(view.getRx(), view.getRy(), view.getRz(), view.getRw(), this.alpha);
                entry.setScale(view.getSx(), view.getSy(), view.getSz(), this.alpha);
            }
        }
    }
}
