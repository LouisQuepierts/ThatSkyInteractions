package net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.AnimationBuffer;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.SkeletonLayout;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

public final class PoseBuffer extends AnimationBuffer.Slice {

    private final View[] views;

    PoseBuffer(
            final AnimationBuffer   buffer,
            final SkeletonLayout layout,
            final int               offset
    ) {
        super(buffer, offset, layout.size() * 16);
        this.views = new View[layout.size()];
        for (int i = 0; i < layout.size(); i++) {
            this.views[i] = new View(buffer, offset + i * 16);
        }
    }

    public PoseView get(int id) {
        return views[id];
    }

    public void copy(final @NonNull PoseBuffer src) {
        this.buffer.memcpy(offset, src.buffer, src.offset, src.size);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class View implements PoseView {
        final AnimationBuffer buffer;
        final int offset;

        @Override
        public void setPosition(final float x, final float y, final float z) {
            buffer.write(offset, x, y, z);
        }

        @Override
        public void setRotationV(final float x, final float y, final float z) {
            buffer.write(offset + 4, x, y, z);
        }

        @Override
        public void setRotationQ(final float x, final float y, final float z, final float w) {
            buffer.write(offset + 4, x, y, z, w);
        }

        @Override
        public void setScale(final float x, final float y, final float z) {
            buffer.write(offset + 8, x, y, z);
        }

        @Override
        public void setPivot(final float x, final float y, final float z) {
            buffer.write(offset + 12, x, y, z);
        }

        @Override
        public void getPosition(final Vector3f out) {
            buffer.readFloat(offset, (x, y, z, _) -> out.set(x, y, z));
        }

        @Override
        public void getRotationV(final Vector3f out) {
            buffer.readFloat(offset + 4, (x, y, z, _) -> out.set(x, y, z));
        }

        @Override
        public void getRotationQ(final Quaternionf out) {
            buffer.readFloat(offset + 4, out::set);
        }

        @Override
        public void getScale(final Vector3f out) {
            buffer.readFloat(offset + 8, (x, y, z, _) -> out.set(x, y, z));
        }

        @Override
        public void getPivot(final Vector3f out) {
            buffer.readFloat(offset + 12, (x, y, z, _) -> out.set(x, y, z));
        }
    }
}
