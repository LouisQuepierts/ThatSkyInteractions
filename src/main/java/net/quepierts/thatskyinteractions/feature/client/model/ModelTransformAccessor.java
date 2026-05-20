package net.quepierts.thatskyinteractions.feature.client.model;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.model.geom.ModelPart;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline.PoseView;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.TransformAccessor;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.TransformProvider;
import org.joml.Math;
import org.joml.Quaternionf;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor
public final class ModelTransformAccessor implements TransformAccessor, TransformProvider {

    public static final boolean OVERRIDE = true;
    private final @NonNull @lombok.NonNull ModelPart part;

    @Override
    public void setPosition(final float x, final float y, final float z) {
        this.part.setPos(x, y, z);
    }

    @Override
    public void setEulerAngle(final float x, final float y, final float z) {
        this.part.setRotation(x, y, z);
    }

    @Override
    public void setQuaternion(final float x, final float y, final float z, final float w) {
        // ZYX
        float eulerX = org.joml.Math.atan2(y * z + w * x, 0.5f - x * x - y * y);
        float eulerY = org.joml.Math.safeAsin(-2.0f * (x * z - w * y));
        float eulerZ = Math.atan2(x * y + w * z, 0.5f - y * y - z * z);

        this.part.setRotation(
                eulerX,
                eulerY,
                eulerZ
        );
    }

    @Override
    public void setScale(final float x, final float y, final float z) {
        this.part.xScale = x;
        this.part.yScale = y;
        this.part.zScale = z;
    }

    @Override
    public void write(final PoseView target) {
        final var part = this.part;
        if (OVERRIDE) {
            final var initial = part.getInitialPose();
            target.setPosition(initial.x(), initial.y(), initial.z());
            final var quaternion = new Quaternionf();
            quaternion.rotateZYX(initial.zRot(), initial.yRot(), initial.xRot());
            target.setRotation(quaternion);
            target.setScale(initial.xScale(), initial.yScale(), initial.zScale());
        } else {
            target.setPosition(part.x, part.y, part.z);
            final var quaternion = new Quaternionf();
            quaternion.rotateZYX(part.zRot, part.yRot, part.xRot);
            target.setRotation(quaternion);
            target.setScale(part.xScale, part.yScale, part.zScale);
        }
    }
}
