package net.quepierts.thatskyinteractions.infra.animation.backend.buffer;

@SuppressWarnings("unused")
public interface WritableBuffer {

    void write(int location, float value);

    void write(int location, float x, float y);

    void write(int location, float x, float y, float z);

    void write(int location, float x, float y, float z, float w);

    void write(int location, int value);

    void write(int location, int x, int y);

    void write(int location, int x, int y, int z);

    void write(int location, int x, int y, int z, int w);

    default void write(int location, boolean value) {
        this.write(location, value ? 1 : 0);
    }

}
