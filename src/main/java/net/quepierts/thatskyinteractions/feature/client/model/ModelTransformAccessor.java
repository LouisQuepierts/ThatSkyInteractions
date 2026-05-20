package net.quepierts.thatskyinteractions.feature.client.model;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.model.geom.ModelPart;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.TransformAccessor;
import org.joml.Math;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor
public final class ModelTransformAccessor implements TransformAccessor {

    private final @NonNull @lombok.NonNull ModelPart part;

    @Override
    public void setPosition(final float x, final float y, final float z) {

        final var pose = this.part.getInitialPose();
        this.part.setPos(
                pose.x() + x,
                pose.y() + y,
                pose.z() + z
        );

    }

    @Override
    public void setEulerAngle(final float x, final float y, final float z) {

        final var pose = this.part.getInitialPose();
        this.part.setRotation(
                pose.xRot() + x,
                pose.yRot() + y,
                pose.zRot() + z
        );

    }

    @Override
    public void setQuaternion(final float x, final float y, final float z, final float w) {

        // ZYX
        float eulerX = org.joml.Math.atan2(y * z + w * x, 0.5f - x * x - y * y);
        float eulerY = org.joml.Math.safeAsin(-2.0f * (x * z - w * y));
        float eulerZ = Math.atan2(x * y + w * z, 0.5f - y * y - z * z);

        final var pose = this.part.getInitialPose();
        this.part.setRotation(
                pose.xRot() + eulerX,
                pose.yRot() + eulerY,
                pose.zRot() + eulerZ
        );
    }

    @Override
    public void setScale(final float x, final float y, final float z) {
        this.part.xScale = x;
        this.part.yScale = y;
        this.part.zScale = z;
    }
}
