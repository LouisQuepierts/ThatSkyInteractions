package net.quepierts.thatskyinteractions.feature.client.model;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.model.geom.ModelPart;
import net.quepierts.thatskyinteractions.infra.animation.adapter.PropertyAccessor;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor(staticName = "of")
public final class PositionAccessor implements PropertyAccessor {

    private final @NonNull @lombok.NonNull ModelPart part;

    @Override
    public void accept(float x, float y, float z, float w) {
        this.part.setPos(x, y, z);
    }
}
