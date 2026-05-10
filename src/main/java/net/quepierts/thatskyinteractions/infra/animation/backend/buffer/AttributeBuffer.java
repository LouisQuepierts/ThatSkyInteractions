package net.quepierts.thatskyinteractions.infra.animation.backend.buffer;

import lombok.Getter;
import net.quepierts.thatskyinteractions.infra.animation.adapter.Consumer4f;
import net.quepierts.thatskyinteractions.infra.animation.adapter.Consumer4i;
import org.jspecify.annotations.NonNull;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@Getter
public final class AttributeBuffer implements ReadableBuffer, WritableBuffer {

    private final IntBuffer bufferView;
    private final int[]     buffer;
    private final int       size;

    public AttributeBuffer(int size) {
        this.buffer     = new int[size];
        this.size       = size;

        this.bufferView = IntBuffer.wrap(this.buffer);
    }

    @Override
    public float readFloat(int location) {
        return Float.intBitsToFloat(this.buffer[location]);
    }

    @Override
    public void readFloat(int location, @NonNull Consumer4f consumer) {
        consumer.accept(
                Float.intBitsToFloat(this.buffer[location]),
                Float.intBitsToFloat(this.buffer[location + 1]),
                Float.intBitsToFloat(this.buffer[location + 2]),
                Float.intBitsToFloat(this.buffer[location + 3])
        );
    }

    @Override
    public void readFloat(int location, int length, float @NonNull [] out) {
        for (int i = 0; i < length; i++) {
            out[i] = Float.intBitsToFloat(this.buffer[location + i]);
        }
    }

    @Override
    public void readFloat(int location, int length, @NonNull FloatBuffer out) {
        for (int i = 0; i < length; i++) {
            out.put(i, Float.intBitsToFloat(this.buffer[location + i]));
        }
    }

    @Override
    public int readInt(int location) {
        return this.buffer[location];
    }

    @Override
    public void readInt(int location, @NonNull Consumer4i consumer) {
        consumer.accept(
                this.buffer[location],
                this.buffer[location + 1],
                this.buffer[location + 2],
                this.buffer[location + 3]
        );
    }

    @Override
    public void readInt(int location, int length, int @NonNull [] out) {
        System.arraycopy(this.buffer, location, out, 0, length);
    }

    @Override
    public void readInt(int location, int length, @NonNull IntBuffer out) {
        out.put(this.buffer, location, length);
    }

    @Override
    public void write(int location, float value) {
        this.buffer[location] = Float.floatToRawIntBits(value);
    }

    @Override
    public void write(int location, float x, float y) {
        this.buffer[location] = Float.floatToRawIntBits(x);
        this.buffer[location + 1] = Float.floatToRawIntBits(y);
    }

    @Override
    public void write(int location, float x, float y, float z) {
        this.buffer[location] = Float.floatToRawIntBits(x);
        this.buffer[location + 1] = Float.floatToRawIntBits(y);
        this.buffer[location + 2] = Float.floatToRawIntBits(z);
    }

    @Override
    public void write(int location, float x, float y, float z, float w) {
        this.buffer[location] = Float.floatToRawIntBits(x);
        this.buffer[location + 1] = Float.floatToRawIntBits(y);
        this.buffer[location + 2] = Float.floatToRawIntBits(z);
        this.buffer[location + 3] = Float.floatToRawIntBits(w);
    }

    @Override
    public void write(int location, int value) {
        this.buffer[location] = value;
    }

    @Override
    public void write(int location, int x, int y) {
        this.buffer[location] = x;
        this.buffer[location + 1] = y;
    }

    @Override
    public void write(int location, int x, int y, int z) {
        this.buffer[location] = x;
        this.buffer[location + 1] = y;
        this.buffer[location + 2] = z;
    }

    @Override
    public void write(int location, int x, int y, int z, int w) {
        this.buffer[location] = x;
        this.buffer[location + 1] = y;
        this.buffer[location + 2] = z;
        this.buffer[location + 3] = w;
    }
}
