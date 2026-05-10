package net.quepierts.thatskyinteractions.infra.animation.backend.uniform;

import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.AnimationBuffer;
import org.jetbrains.annotations.NotNull;
import org.joml.*;

public final class UniformBuffer implements UniformReader {

    private final AnimationBuffer   buffer;
    private final UboDefinition     definition;

    public UniformBuffer(@NotNull UboDefinition definition) {
        this.buffer                 = new AnimationBuffer(definition.getSize());
        this.definition             = definition;
    }

    public void write(int location, float value) {
        var offset                  = this.definition.getUniformOffset(location);
        this.buffer                 .write(offset, value);
    }

    public void write(int location, float x, float y) {
        var offset                  = this.definition.getUniformOffset(location);
        this.buffer                 .write(offset, x, y);
    }

    public void write(int location, float x, float y, float z) {
        var offset                  = this.definition.getUniformOffset(location);
        this.buffer                 .write(offset, x, y, z);
    }

    public void write(int location, float x, float y, float z, float w) {
        var offset                  = this.definition.getUniformOffset(location);
        this.buffer                 .write(offset, x, y, z, w);
    }
    
    public void write(int location, Vector2fc vector2f) {
        var offset                  = this.definition.getUniformOffset(location);
        vector2f                    .get(this.buffer.getBufferView().position(offset));
    }
    
    public void write(int location, Vector3fc vector3f) {
        var offset                  = this.definition.getUniformOffset(location);
        vector3f                    .get(this.buffer.getBufferView().position(offset));
    }
    
    public void write(int location, Vector4fc vector4f) {
        var offset                  = this.definition.getUniformOffset(location);
        vector4f                    .get(this.buffer.getBufferView().position(offset));
    }

    public void write(int location, int value) {
        var offset                  = this.definition.getUniformOffset(location);
        this.buffer                 .write(offset, Float.intBitsToFloat(value));
    }

    public void write(int location, int x, int y) {
        var offset                  = this.definition.getUniformOffset(location);
        this.buffer                 .write(
                                        offset,
                                        Float.intBitsToFloat(x),
                                        Float.intBitsToFloat(y)
                                    );
    }

    public void write(int location, int x, int y, int z) {
        var offset                  = this.definition.getUniformOffset(location);
        this.buffer                 .write(
                                        offset,
                                        Float.intBitsToFloat(x),
                                        Float.intBitsToFloat(y),
                                        Float.intBitsToFloat(z)
                                    );
    }

    public void write(int location, int x, int y, int z, int w) {
        var offset                  = this.definition.getUniformOffset(location);
        this.buffer                 .write(
                                        offset,
                                        Float.intBitsToFloat(x),
                                        Float.intBitsToFloat(y),
                                        Float.intBitsToFloat(z),
                                        Float.intBitsToFloat(w)
                                    );
    }
    
    public void write(int location, Vector2i vector2i) {
        var offset                  = this.definition.getUniformOffset(location);
        this.buffer                 .write(
                                        offset,
                                        Float.intBitsToFloat(vector2i.x()),
                                        Float.intBitsToFloat(vector2i.y())
                                    );
    }
    
    public void write(int location, Vector3i vector3i) {
        var offset                  = this.definition.getUniformOffset(location);
        this.buffer                 .write(
                                        offset,
                                        Float.intBitsToFloat(vector3i.x()),
                                        Float.intBitsToFloat(vector3i.y()),
                                        Float.intBitsToFloat(vector3i.z())
                                    );
    }
    
    public void write(int location, Vector4i vector4i) {
        var offset                  = this.definition.getUniformOffset(location);
        this.buffer                 .write(
                                        offset,
                                        Float.intBitsToFloat(vector4i.x()),
                                        Float.intBitsToFloat(vector4i.y()),
                                        Float.intBitsToFloat(vector4i.z()),
                                        Float.intBitsToFloat(vector4i.w())
                                    );
    }

    public void write(int location, boolean value) {
        var offset                  = this.definition.getUniformOffset(location);
        this.buffer                 .write(offset, value ? 1.0f : 0.0f);
    }
    
    public void write(int location, Matrix2fc matrix2f) {
        var offset                  = this.definition.getUniformOffset(location);
        matrix2f                    .get(this.buffer.getBufferView().position(offset));
    }
    
    public void write(int location, Matrix3fc matrix3f) {
        var offset                  = this.definition.getUniformOffset(location);
        matrix3f                    .get(this.buffer.getBufferView().position(offset));
    }
    
    public void write(int location, Matrix4fc matrix4f) {
        var offset                  = this.definition.getUniformOffset(location);
        matrix4f                    .get(this.buffer.getBufferView().position(offset));
    }

    @Override
    public int readInt(int location) {
        var offset                  = this.definition.getUniformOffset(location);
        return Float                .floatToRawIntBits(this.buffer.readFloat(offset));
    }

    @Override
    public float readFloat(int location) {
        var offset                  = this.definition.getUniformOffset(location);
        return this.buffer          .readFloat(offset);
    }

    @Override
    public boolean readBool(int location) {
        var offset                  = this.definition.getUniformOffset(location);
        return this.buffer          .readFloat(offset) != 0.0f;
    }

    @Override
    public void read(int location, UniformType type, int[] out) {
        var offset                  = this.definition.getUniformOffset(location);
        for (var i = 0; i < out.length; i++) {
            out[i]                  = Float.floatToRawIntBits(this.buffer.readFloat(offset + i));
        }
    }

    @Override
    public void read(int location, UniformType type, float[] out) {
        var offset                  = this.definition.getUniformOffset(location);
        this.buffer                 .getBufferView()
                                    .get(out, offset, type.getSize());
    }

    @Override
    public void read(
            int location,
            UniformType type,
            int offset,
            AnimationBuffer out
    ) {
        this.buffer                 .memcpy(
                                        offset,
                                        out,
                                        location,
                                        type.getSize()
                                    );
    }
}
