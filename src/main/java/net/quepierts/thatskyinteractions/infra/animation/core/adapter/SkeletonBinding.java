package net.quepierts.thatskyinteractions.infra.animation.core.adapter;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline.SkeletonPoseBuffer;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline.SkeletonResultView;
import net.quepierts.thatskyinteractions.infra.animation.core.skeleton.PoseCache;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SkeletonBinding {

    private final Entry[] entries;

    public static Builder builder() {
        return new Builder();
    }

    public void apply(@NonNull final SkeletonResultView view) {
        for (var entry : this.entries) {
            final var pose = view.get(entry.location());
            pose.getTransform(entry.accessor());
        }
    }

    public void apply(@NonNull final PoseCache cache) {
        for (var entry : this.entries) {
            final var pose = cache.get(entry.location());
            final var accessor = entry.accessor;
            accessor.setPosition(pose.getTx(), pose.getTy(), pose.getTz());
            accessor.setQuaternion(pose.getRx(), pose.getRy(), pose.getRz(), pose.getRw());
            accessor.setScale(pose.getSx(), pose.getSy(), pose.getSz());
        }
    }

    public void fetch(@NonNull final SkeletonPoseBuffer target) {
        for (final var entry : this.entries) {
            final var view = target.get(entry.location());
            entry.provider().write(view);
        }
    }

    record Entry(
            int                 location,
            TransformAccessor   accessor,
            TransformProvider   provider
    ) { }

    public static final class Builder {
        private final List<Entry> entries = new ArrayList<>();

        public @NonNull Builder bind(
                int location,
                @NonNull TransformAccessor accessor,
                @NonNull TransformProvider provider
        ) {
            if (location > -1) {
                this.entries.add(new Entry(location, accessor, provider));
            }
            return this;
        }

        public @NonNull Builder bind(
                int location,
                @NonNull Supplier<TransformAccessor> supplier,
                @NonNull Supplier<TransformProvider> provider
        ) {
            if (location > -1) {
                this.entries.add(new Entry(location, supplier.get(), provider.get()));
            }
            return this;
        }

        public @NonNull SkeletonBinding build() {
            return new SkeletonBinding(entries.toArray(Entry[]::new));
        }
    }

}
