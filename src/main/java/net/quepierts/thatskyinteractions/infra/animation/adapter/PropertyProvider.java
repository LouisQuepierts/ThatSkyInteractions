package net.quepierts.thatskyinteractions.infra.animation.adapter;

import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.WritableBuffer;
import org.jspecify.annotations.NonNull;

@FunctionalInterface
public interface PropertyProvider {

    void write(final int offset, final @NonNull WritableBuffer target);

}
