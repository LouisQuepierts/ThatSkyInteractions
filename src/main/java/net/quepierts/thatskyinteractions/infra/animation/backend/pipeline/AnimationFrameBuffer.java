package net.quepierts.thatskyinteractions.infra.animation.backend.pipeline;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.AnimationBuffer;
import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.WritableBuffer;

public final class AnimationFrameBuffer extends AnimationBuffer.Slice {

    AnimationFrameBuffer(AnimationBuffer buffer, int offset, int size) {
        super(buffer, offset, size);
    }

    public void memcpy(int dstOffset, AnimationFrameBuffer src, int length) {
        System.arraycopy(
                src.buffer.getBuffer(),
                src.offset,
                this.buffer.getBuffer(),
                this.offset + dstOffset,
                length
        );
    }

    public void memcpy(int dstOffset, AnimationFrameBuffer src, int srcOffset, int length) {
        System.arraycopy(
                src.buffer.getBuffer(),
                src.offset + srcOffset,
                this.buffer.getBuffer(),
                this.offset + dstOffset,
                length
        );
    }

    public static void memcpy(AnimationFrameBuffer src, AnimationFrameBuffer dst) {
        System.arraycopy(
                src.buffer.getBuffer(),
                src.offset,
                dst.buffer.getBuffer(),
                dst.offset,
                src.size
        );
    }

    public static void blend(
            AnimationFrameBuffer src0,
            AnimationFrameBuffer src1,
            AnimationFrameBuffer dst,
            float weight
    ) {
        if (weight == 0.0f) {
            memcpy(src0, dst);
        } else if (weight == 1.0f) {
            memcpy(src1, dst);
        } else {
            var invWeight   = 1 - weight;
            var dstBuffer   = dst.buffer.getBuffer();
            var src0Buffer  = src0.buffer.getBuffer();
            var src1Buffer  = src1.buffer.getBuffer();

            var i           = dst.offset;
            var j           = src0.offset;
            var k           = src1.offset;

            var t           = dst.offset + dst.size;
            for (; i < t; i++, j++, k++) {
                dstBuffer[i] = src0Buffer[j] * invWeight + src1Buffer[k] * weight;
            }
        }
    }

    @Override
    public void write(int location, float value) {
        buffer.write(location, value);
    }

    @Override
    public void write(int location, float x, float y) {
        buffer.write(location, x, y);
    }

    @Override
    public void write(int location, float x, float y, float z) {
        buffer.write(location, x, y, z);
    }

    @Override
    public void write(int location, float x, float y, float z, float w) {
        buffer.write(location, x, y, z, w);
    }

    @Override
    public void write(int location, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(int location, int x, int y) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(int location, int x, int y, int z) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(int location, int x, int y, int z, int w) {
        throw new UnsupportedOperationException();
    }
}
