package net.quepierts.thatskyinteractions.feature.data.packet;

import io.netty.buffer.Unpooled;
import lombok.NoArgsConstructor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.quepierts.veynir.core.misc.Generic;
import org.jspecify.annotations.NonNull;

@NoArgsConstructor
public final class PacketCache {

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketCache> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public PacketCache decode(final RegistryFriendlyByteBuf byteBuf) {
            var cache = new PacketCache();
            cache.read(byteBuf);
            return cache;
        }

        @Override
        public void encode(final RegistryFriendlyByteBuf byteBuf, final PacketCache packetCache) {
            packetCache.write(byteBuf);
        }
    };

    private volatile RegistryFriendlyByteBuf buffer;
    private int readableBytes;

    private StreamCodec<? super RegistryFriendlyByteBuf, ?> cachedCodec;
    private Object cachedObject;

    public void cache(
            @NonNull final StreamCodec<? super RegistryFriendlyByteBuf, ?> codec,
            @NonNull final Object object
    ) {
        this.cachedCodec = codec;
        this.cachedObject = object;

        this.free();
    }

    /*public <T> void encode(
            @NonNull final StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            @NonNull final T object
    ) {
        var fresh = new RegistryFriendlyByteBuf(Unpooled.buffer(), )
        codec.encode(fresh, object);

        this.free();

        this.buffer = fresh;
        this.readableBytes = fresh.readableBytes();
    }*/

    public <T> T decode(
            @NonNull final StreamCodec<? super RegistryFriendlyByteBuf, T> codec
    ) {
        return codec.decode(this.buffer);
    }

    public void write(
            @NonNull final RegistryFriendlyByteBuf target
    ) {
        if (this.buffer == null) {

            this.buffer = new RegistryFriendlyByteBuf(
                    Unpooled.buffer(),
                    target.registryAccess(),
                    target.getConnectionType()
            );

            if (this.cachedCodec != null && this.cachedObject != null) {
                this.cachedCodec.encode(this.buffer, Generic.cast(this.cachedObject));
                this.cachedCodec = null;
                this.cachedObject = null;
                this.readableBytes = this.buffer.readableBytes();
            }

        }
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

    public void read(final RegistryFriendlyByteBuf byteBuf) {
        this.free();
        final var bytes = byteBuf.readableBytes();
        this.buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(bytes), byteBuf.registryAccess(), byteBuf.getConnectionType());
        this.buffer.writeBytes(byteBuf);
        this.readableBytes = bytes;
    }
}
