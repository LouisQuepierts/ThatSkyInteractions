package net.quepierts.thatskyinteractions.feature.client.model;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.model.geom.ModelPart;
import net.quepierts.thatskyinteractions.infra.animation.adapter.PropertyProvider;
import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.WritableBuffer;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor(staticName = "of")
public final class PositionProvider implements PropertyProvider {

    private final @NonNull @lombok.NonNull ModelPart    part;
    private final @NonNull @lombok.NonNull String       channel;

    @Override
    public void write(final int offset, final @NonNull WritableBuffer target) {
        target.write(
                offset,
                this.part.x,
                this.part.y,
                this.part.z
        );
    }
}
