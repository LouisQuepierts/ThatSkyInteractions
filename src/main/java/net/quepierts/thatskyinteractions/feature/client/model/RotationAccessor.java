package net.quepierts.thatskyinteractions.feature.client.model;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.quepierts.thatskyinteractions.infra.animation.adapter.PropertyAccessor;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor(staticName = "of")
public final class RotationAccessor implements PropertyAccessor {

    private final @NonNull @lombok.NonNull  ModelPart   part;
    private final @NonNull @lombok.NonNull  String      channel;

    @Override
    public void accept(float x, float y, float z, float w) {
        final var pose = this.part.getInitialPose();
        this.part.setRotation(
                pose.xRot() + x * Mth.DEG_TO_RAD,
                pose.yRot() + y * Mth.DEG_TO_RAD,
                pose.zRot() + z * Mth.DEG_TO_RAD
        );
    }

}
