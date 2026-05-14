package net.quepierts.thatskyinteractions.feature.client.model;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.model.geom.ModelPart;
import net.quepierts.thatskyinteractions.infra.animation.adapter.PropertyAccessor;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor(staticName = "of")
public final class PositionAccessor implements PropertyAccessor {

    private final @NonNull @lombok.NonNull ModelPart    part;
    private final @NonNull @lombok.NonNull String       channel;

    @Override
    public void accept(float x, float y, float z, float w) {
        final var pose = this.part.getInitialPose();
        this.part.setPos(
                pose.x() + x * 0.0625f,
                pose.y() + y * 0.0625f,
                pose.z() + z * 0.0625f
        );
    }
}
