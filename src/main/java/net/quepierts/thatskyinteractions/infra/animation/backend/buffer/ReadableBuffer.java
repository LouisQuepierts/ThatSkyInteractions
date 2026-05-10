package net.quepierts.thatskyinteractions.infra.animation.backend.buffer;

import net.quepierts.thatskyinteractions.infra.animation.adapter.Consumer4f;
import net.quepierts.thatskyinteractions.infra.animation.adapter.Consumer4i;
import org.jspecify.annotations.NonNull;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@SuppressWarnings("unused")
public interface ReadableBuffer {

    float   readFloat(int location);

    void    readFloat(int location, @NonNull Consumer4f consumer);

    void    readFloat(int location, int length, float @NonNull[] out);

    void    readFloat(int location, int length, @NonNull FloatBuffer out);

    int     readInt(int location);

    void    readInt(int location, @NonNull Consumer4i consumer);

    void    readInt(int location, int length, int @NonNull[] out);

    void    readInt(int location, int length, @NonNull IntBuffer out);

    default boolean readBoolean(int location) {
        return readInt(location) != 0;
    }

}
