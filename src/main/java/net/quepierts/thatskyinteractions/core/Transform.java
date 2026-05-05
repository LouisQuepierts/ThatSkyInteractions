package net.quepierts.thatskyinteractions.core;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

public interface Transform {

    void setPosition(float x, float y, float z);

    void setRotation(float x, float y, float z);

    void setScale(float x, float y, float z);

    void setPivot(float x, float y, float z);

    void setParent(@Nullable Transform parent);

    Vector3f getPosition();

    Vector3f getEulerAngle();

    Vector3f getScale();

    Vector3f getPivot();

    boolean isTranslated();

    boolean isRotated();

    boolean isScaled();

    boolean isPivoted();

    @Nullable Transform getParent();

    default void getMatrix(Matrix4f matrix) {
        if (this.isTranslated()) {
            var position    = this.getPosition();
            matrix          .translate(
                            position.x(),
                            position.y(),
                            position.z()
            );
        }

        if (this.isPivoted()) {
            var pivot       = this.getPivot();
            matrix          .translate(
                            -pivot.x(),
                            -pivot.y(),
                            -pivot.z()
            );
        }

        if (this.isRotated()) {
            var euler       = this.getEulerAngle();
            matrix          .rotateXYZ(
                    euler.x() * (float) Math.PI / 180.0f,
                    euler.y() * (float) Math.PI / 180.0f,
                    euler.z() * (float) Math.PI / 180.0f
            );
        }

        if (this.isScaled()) {
            var scale       = this.getScale();
            matrix          .scale(
                            scale.x(),
                            scale.y(),
                            scale.z()
            );
        }

        if (this.isPivoted()) {
            var pivot       = this.getPivot();
            matrix          .translate(
                            -pivot.x(),
                            -pivot.y(),
                            -pivot.z()
            );
        }
    }

}
