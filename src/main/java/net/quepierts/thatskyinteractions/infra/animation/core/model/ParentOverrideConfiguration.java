package net.quepierts.thatskyinteractions.infra.animation.core.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.SkeletonLayout;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ParentOverrideConfiguration {

    private final float[] parent;

    public static Builder builder(@NonNull final SkeletonLayout layout) {
        return new Builder(layout);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Builder {

        private final @NonNull @lombok.NonNull SkeletonLayout layout;
        private final Map<String, String> overrides = new HashMap<>();

        public Builder override(
                @NonNull final String bone,
                @NonNull final String parent
        ) {
            final var bid = this.layout.id(bone);
            final var pid = this.layout.id(parent);

            if (bid == -1) {
                throw new IllegalArgumentException("Bone " + bone + " does not exist.");
            }

            if (pid == -1) {
                throw new IllegalArgumentException("Bone " + parent + " does not exist.");
            }

            this.overrides.put(bone, parent);
            return this;
        }

        public ParentOverrideConfiguration build() {
            final var size      = this.layout.size();
            final var parent    = new float[size];
            Arrays.fill(parent, -1);

            for (final var entry : this.overrides.entrySet()) {
                final var bid   = this.layout.id(entry.getKey());
                final var pid   = this.layout.id(entry.getValue());
                parent[bid]     = pid;
            }

            return new ParentOverrideConfiguration(parent);
        }

    }

}
