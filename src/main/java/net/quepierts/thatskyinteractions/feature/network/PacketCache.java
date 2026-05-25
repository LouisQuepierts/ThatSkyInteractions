package net.quepierts.thatskyinteractions.feature.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.NonNull;

public final class PacketCache {

    private volatile ByteBuf buffer;
    private int readableBytes;

    public PacketCache() {}

    private PacketCache(int capacity) {
        this.buffer = Unpooled.buffer(capacity);
    }

    public <T> void encode(
            @NonNull final StreamCodec<ByteBuf, T> codec,
            @NonNull final T object
    ) {
        var fresh = Unpooled.buffer();
        codec.encode(fresh, object);

        this.free();

        this.buffer = fresh;
        this.readableBytes = fresh.readableBytes();
    }

    public <T> T decode(
            @NonNull final StreamCodec<ByteBuf, T> codec
    ) {
        return codec.decode(this.buffer);
    }

    public void write(
            @NonNull final ByteBuf target
    ) {
        target.writeBytes(this.buffer, 0, this.readableBytes);
    }

    public boolean ready() {
        return this.buffer != null && this.buffer.isReadable();
    }

    public void free() {
        if (this.buffer != null) {
            this.buffer.release();
            this.buffer = null;
        }
    }

    public void read(final ByteBuf byteBuf) {
        this.free();
        this.buffer = Unpooled.buffer(byteBuf.readableBytes());
        this.buffer.writeBytes(byteBuf);
    }
}
