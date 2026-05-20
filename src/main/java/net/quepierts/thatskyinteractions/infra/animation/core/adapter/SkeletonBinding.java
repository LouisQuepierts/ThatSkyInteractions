package net.quepierts.thatskyinteractions.infra.animation.core.adapter;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline.SkeletonResultView;
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

    public void apply(@NonNull SkeletonResultView view) {
        for (var entry : this.entries) {
            final var pose = view.get(entry.location());
            pose.getTransform(entry.accessor());
        }
    }


    record Entry(
            int                 location,
            TransformAccessor   accessor
    ) { }

    public static final class Builder {
        private final List<Entry> entries = new ArrayList<>();

        public @NonNull Builder bind(int location, @NonNull TransformAccessor accessor) {
            if (location > -1) {
                this.entries.add(new Entry(location, accessor));
            }
            return this;
        }

        public @NonNull Builder bind(int location, @NonNull Supplier<TransformAccessor> supplier) {
            if (location > -1) {
                this.entries.add(new Entry(location, supplier.get()));
            }
            return this;
        }

        public @NonNull SkeletonBinding build() {
            return new SkeletonBinding(entries.toArray(Entry[]::new));
        }
    }

}
