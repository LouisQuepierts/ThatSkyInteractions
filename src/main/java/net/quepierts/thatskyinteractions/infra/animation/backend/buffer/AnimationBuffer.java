package net.quepierts.thatskyinteractions.infra.animation.backend.buffer;

import lombok.Getter;
import net.quepierts.thatskyinteractions.infra.animation.adapter.Consumer4f;

import java.nio.FloatBuffer;

public final class AnimationBuffer {

    public static final AnimationBuffer DUMMY   = new AnimationBuffer(0);

    @Getter
    private final   FloatBuffer bufferView;
    private final   float[]     buffer;
    private final   int         size;

    public AnimationBuffer(int size) {
        this.buffer     = new float[size];
        this.size       = size;

        this.bufferView = FloatBuffer.wrap(this.buffer);
    }

    public void write(int index, float value) {
        this.buffer[index] = value;
    }

    public void write(int index, float x, float y) {
        this.buffer[index] = x;
        this.buffer[index + 1] = y;
    }

    public void write(int index, float x, float y, float z) {
        this.buffer[index] = x;
        this.buffer[index + 1] = y;
        this.buffer[index + 2] = z;
    }

    public void write(int index, float x, float y, float z, float w) {
        this.buffer[index] = x;
        this.buffer[index + 1] = y;
        this.buffer[index + 2] = z;
        this.buffer[index + 3] = w;
    }

    public float read(int location) {
        return this.buffer[location];
    }

    public void read(int location, Consumer4f consumer) {
        consumer.accept(
                this.buffer[location],
                this.buffer[location + 1],
                this.buffer[location + 2],
                this.buffer[location + 3]
        );
    }

    public void read(int location, int length, float[] out) {
        System.arraycopy(this.buffer, location, out, 0, length);
    }

    public void read(int location, int length, FloatBuffer out) {
        out.put(this.buffer, location, length);
    }

    public void memcpy(int dstOffset, AnimationBuffer src, int length) {
        System.arraycopy(src.buffer, 0, this.buffer, dstOffset, length);
    }

    public void memcpy(int dstOffset, AnimationBuffer src, int srcOffset, int length) {
        System.arraycopy(src.buffer, srcOffset, this.buffer, dstOffset, length);
    }

    public boolean check(int index, int length) {
        return index >= 0 && index + length <= this.size;
    }

    public static void memcpy(AnimationBuffer src, AnimationBuffer dst) {
        System.arraycopy(src.buffer, 0, dst.buffer, 0, Math.min(src.size, dst.size));
    }

    public static void blend(
            AnimationBuffer src0,
            AnimationBuffer src1,
            AnimationBuffer dst,
            float weight
    ) {
        if (weight == 0.0f) {
            memcpy(src0, dst);
        } else if (weight == 1.0f) {
            memcpy(src1, dst);
        } else {
            var invWeight = 1 - weight;
            for (int i = 0; i < src0.size; i++) {
                dst.buffer[i] = src0.buffer[i] * weight + src1.buffer[i] * invWeight;
            }
        }
    }

    public AnimationBufferSlice slice(int offset, int length) {
        return new AnimationBufferSlice(this, offset, length);
    }

    public AnimationBufferSlice slice() {
        return new AnimationBufferSlice(this, 0, this.size);
    }
}
