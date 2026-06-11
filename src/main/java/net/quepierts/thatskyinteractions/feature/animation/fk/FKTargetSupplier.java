package net.quepierts.thatskyinteractions.feature.animation.fk;

import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

@FunctionalInterface
public interface FKTargetSupplier {

    void get(
            final @NonNull Vector3f out,
            final          float    partialTick
    );

}
