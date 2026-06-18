package net.quepierts.thatskyinteractions.feature.animation.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.quepierts.veynir.backend.skeleton.SkeletonLayout;
import net.quepierts.veynir.core.adapter.TransformAccessor;
import org.joml.Math;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public final class ModelSkeleton {

    @Getter
    private final SkeletonLayout        layout;
    private final Map<String, Bone>     byName;
    private final List<Bone>            byId;

    public Bone get(@NonNull String name) {
        return this.byName.get(name);
    }

    public Bone get(int id) {
        return this.byId.get(id);
    }

    public List<Bone> getEntries() {
        return this.byId;
    }

    public record Bone(
            String      name,
            Delegate    part,
            int         id
    ) implements TransformAccessor {

        @Override
        public void setPosition(
                final float x,
                final float y,
                final float z
        ) {
            this.part.setPosition(x, y, z);
        }

        @Override
        public void setEulerAngle(
                final float x,
                final float y,
                final float z
        ) {
            this.part.setRotation(x, y, z);
        }

        @Override
        public void setQuaternion(
                final float x,
                final float y,
                final float z,
                final float w
        ) {
            float eulerX = Math.atan2(y * z + w * x, 0.5f - x * x - y * y);
            float eulerY = Math.safeAsin(-2.0f * (x * z - w * y));
            float eulerZ = Math.atan2(x * y + w * z, 0.5f - y * y - z * z);

            this.part.setRotation(
                    eulerX,
                    eulerY,
                    eulerZ
            );
        }

        @Override
        public void setScale(
                final float x,
                final float y,
                final float z
        ) {
            this.part.setScale(x, y, z);
        }

        public void setPosition(
                final float x,
                final float y,
                final float z,
                final float alpha
        ) {
            final var part = this.part;
            if (alpha == 1.0f) {
                part.setPosition(x, y, z);
            } else if (alpha > 0.0f) {
                part.setPosition(
                        part.x() * (1.0f - alpha) + x * alpha,
                        part.y() * (1.0f - alpha) + y * alpha,
                        part.z() * (1.0f - alpha) + z * alpha
                );
            }
        }

        public void setQuaternion(
                final float x,
                final float y,
                final float z,
                final float w,
                final float alpha
        ) {
            if (alpha == 1.0f) {
                this.setQuaternion(x, y, z, w);
            } else if (alpha > 0.0f) {
                float eulerX = Math.atan2(y * z + w * x, 0.5f - x * x - y * y);
                float eulerY = Math.safeAsin(-2.0f * (x * z - w * y));
                float eulerZ = Math.atan2(x * y + w * z, 0.5f - y * y - z * z);

                final var part = this.part;
                part.setRotation(
                        part.xRot() * (1.0f - alpha) + eulerX * alpha,
                        part.yRot() * (1.0f - alpha) + eulerY * alpha,
                        part.zRot() * (1.0f - alpha) + eulerZ * alpha
                );
            }
        }

        public void setScale(
                final float x,
                final float y,
                final float z,
                final float alpha
        ) {
            final var part = this.part;
            if (alpha == 1.0f) {
                part.setScale(x, y, z);
            } else if (alpha > 0.0f) {
                part.setScale(
                        part.xScale() * (1.0f - alpha) + x * alpha,
                        part.yScale() * (1.0f - alpha) + y * alpha,
                        part.zScale() * (1.0f - alpha) + z * alpha
                );
            }
        }

    }

    public interface Delegate {
        void setPosition(float x, float y, float z);

        void setRotation(float x, float y, float z);

        void setScale(float x, float y, float z);

        float x();

        float y();

        float z();

        float xRot();

        float yRot();

        float zRot();

        float xScale();

        float yScale();

        float zScale();

        Delegate getInitialPose();
    }
}
