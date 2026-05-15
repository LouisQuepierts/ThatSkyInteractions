package net.quepierts.thatskyinteractions.infra.animation.backend.buffer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.Consumer4f;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.Consumer4i;
import org.jspecify.annotations.NonNull;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@Getter
public final class AnimationBuffer implements ReadableBuffer, WritableBuffer {

    public static final AnimationBuffer DUMMY   = new AnimationBuffer(0);

    private final   FloatBuffer bufferView;
    private final   float[]     buffer;
    private final   int         size;

    public AnimationBuffer(int size) {
        this.buffer     = new float[size];
        this.size       = size;

        this.bufferView = FloatBuffer.wrap(this.buffer);
    }

    @Override
    public void write(int location, float value) {
        this.buffer[location] = value;
    }

    @Override
    public void write(int location, float x, float y) {
        this.buffer[location] = x;
        this.buffer[location + 1] = y;
    }

    @Override
    public void write(int location, float x, float y, float z) {
        this.buffer[location] = x;
        this.buffer[location + 1] = y;
        this.buffer[location + 2] = z;
    }

    @Override
    public void write(int location, float x, float y, float z, float w) {
        this.buffer[location] = x;
        this.buffer[location + 1] = y;
        this.buffer[location + 2] = z;
        this.buffer[location + 3] = w;
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

    @Override
    public float readFloat(int location) {
        return this.buffer[location];
    }

    @Override
    public void readFloat(int location, @NonNull Consumer4f consumer) {
        consumer.accept(
                this.buffer[location],
                this.buffer[location + 1],
                this.buffer[location + 2],
                this.buffer[location + 3]
        );
    }

    @Override
    public void readFloat(int location, int length, float @NonNull [] out) {
        System.arraycopy(this.buffer, location, out, 0, length);
    }

    @Override
    public void readFloat(int location, int length, @NonNull FloatBuffer out) {
        out.put(this.buffer, location, length);
    }

    @Override
    public int readInt(int location) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void readInt(int location, @NonNull Consumer4i consumer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void readInt(int location, int length, int @NonNull [] out) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void readInt(int location, int length, @NonNull IntBuffer out) {
        throw new UnsupportedOperationException();
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

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Slice implements ReadableBuffer, WritableBuffer {

        protected final AnimationBuffer buffer;
        protected final int offset;
        protected final int size;

        public Slice slice(int offset, int size) {
            return new Slice(this.buffer, this.offset + offset, size);
        }

        @Override
        public void write(int location, float value) {
            this.buffer.write(this.offset + location, value);
        }

        @Override
        public void write(int location, float x, float y) {
            this.buffer.write(this.offset + location, x, y);
        }

        @Override
        public void write(int location, float x, float y, float z) {
            this.buffer.write(this.offset + location, x, y, z);
        }

        @Override
        public void write(int location, float x, float y, float z, float w) {
            this.buffer.write(this.offset + location, x, y, z, w);
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

        @Override
        public float readFloat(int location) {
            return this.buffer.readFloat(this.offset + location);
        }

        @Override
        public void readFloat(int location, @NonNull Consumer4f consumer) {
            this.buffer.readFloat(this.offset + location, consumer);
        }

        @Override
        public void readFloat(int location, int length, float @NonNull [] out) {
            this.buffer.readFloat(this.offset + location, length, out);
        }

        @Override
        public void readFloat(int location, int length, @NonNull FloatBuffer out) {
            this.buffer.readFloat(this.offset + location, length, out);
        }

        @Override
        public int readInt(int location) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void readInt(int location, @NonNull Consumer4i consumer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void readInt(int location, int length, int @NonNull [] out) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void readInt(int location, int length, @NonNull IntBuffer out) {
            throw new UnsupportedOperationException();
        }
    }
}
