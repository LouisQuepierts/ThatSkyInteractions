package net.quepierts.thatskyinteractions.feature.data.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.NoArgsConstructor;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.NonNull;

@NoArgsConstructor
public final class PacketCache {

    public static final StreamCodec<ByteBuf, PacketCache> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public PacketCache decode(final ByteBuf byteBuf) {
            var cache = new PacketCache();
            cache.read(byteBuf);
            return cache;
        }

        @Override
        public void encode(final ByteBuf byteBuf, final PacketCache packetCache) {
            packetCache.write(byteBuf);
        }
    };

    private volatile ByteBuf buffer;
    private int readableBytes;

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
        final var bytes = byteBuf.readableBytes();
        this.buffer = Unpooled.buffer(bytes);
        this.buffer.writeBytes(byteBuf);
        this.readableBytes = bytes;
    }
}
