package net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.AnimationBuffer;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.SkeletonLayout;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.TransformAccessor;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

public final class SkeletonPoseBuffer
        extends AnimationBuffer.Slice
        implements SkeletonResultView {

    private final View[] views;

    SkeletonPoseBuffer(
            final AnimationBuffer   buffer,
            final SkeletonLayout    layout,
            final int               offset
    ) {
        super(buffer, offset, layout.size() * SkeletonLayout.BONE_SIZE);
        this.views = new View[layout.size()];
        for (int i = 0; i < layout.size(); i++) {
            this.views[i] = new View(buffer, offset + i * SkeletonLayout.BONE_SIZE);
        }
    }

    public PoseView get(int id) {
        return views[id];
    }

    public void copy(final @NonNull SkeletonPoseBuffer src) {
        if (this == src) {
            return;
        }
        this.buffer.memcpy(offset, src.buffer, src.offset, src.size);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class View implements PoseView {
        final AnimationBuffer buffer;
        final int offset;

        final float[] tmp = new float[SkeletonLayout.BONE_SIZE];

        @Override
        public void setPosition(final float x, final float y, final float z) {
            buffer.write(offset, x, y, z);
        }

        @Override
        public void setRotation(final float x, final float y, final float z, final float w) {
            buffer.write(offset + 4, x, y, z, w);
        }

        @Override
        public void setScale(final float x, final float y, final float z) {
            buffer.write(offset + 8, x, y, z);
        }

        @Override
        public void getPosition(final Vector3f out) {
            buffer.readFloat(offset, 3, this.tmp);
            out.set(this.tmp);
        }

        @Override
        public void getRotation(final Quaternionf out) {
            final var tmp = this.tmp;
            buffer.readFloat(offset + 4, 4, tmp);
            out.set(tmp[0], tmp[1], tmp[2], tmp[3]);
        }

        @Override
        public void getScale(final Vector3f out) {
            buffer.readFloat(offset + 8, 3, this.tmp);
            out.set(this.tmp);
        }

        @Override
        public void getTransform(final TransformAccessor out) {
            final var tmp = this.tmp;
            buffer.readFloat(offset, 12, tmp);
            out.setPosition(tmp[0], tmp[1], tmp[2]);
            out.setQuaternion(tmp[4], tmp[5], tmp[6], tmp[7]);
            out.setScale(tmp[8], tmp[9], tmp[10]);
        }

    }
}
