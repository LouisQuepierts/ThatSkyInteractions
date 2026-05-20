package net.quepierts.thatskyinteractions.infra.animation.core.adapter;

import lombok.Getter;
import org.joml.Quaternionf;

@Getter
public final class TransformF implements TransformAccessor {

    private float tx, ty, tz;
    private float rx, ry, rz, rw;
    private float sx, sy, sz;

    @Override
    public void setPosition(final float x, final float y, final float z) {
        this.tx = x;
        this.ty = y;
        this.tz = z;
    }

    @Override
    public void setEulerAngle(final float x, final float y, final float z) {
        var quaternion = new Quaternionf();
        quaternion.rotateZYX(z, y, x);
        this.rx = quaternion.x;
        this.ry = quaternion.y;
        this.rz = quaternion.z;
        this.rw = quaternion.w;
    }

    @Override
    public void setQuaternion(final float x, final float y, final float z, final float w) {
        this.rx = x;
        this.ry = y;
        this.rz = z;
        this.rw = w;
    }

    @Override
    public void setScale(final float x, final float y, final float z) {
        this.sx = x;
        this.sy = y;
        this.sz = z;
    }
}
