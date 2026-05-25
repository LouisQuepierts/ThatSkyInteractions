package net.quepierts.thatskyinteractions.feature.data;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.quepierts.thatskyinteractions.feature.network.PacketCache;
import net.quepierts.thatskyinteractions.feature.network.SyncDatapackPacket;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public abstract class DataSyncManager<T> extends SimpleJsonResourceReloadListener<T> {

    @Getter
    private final Identifier identifier;

    @Getter
    private final PacketCache cache;

    protected DataSyncManager(
            final Codec<T>                                  codec,
            final FileToIdConverter                         lister,
            final Identifier                                identifier
    ) {
        super(codec, lister);
        this.identifier     = identifier;

        this.cache = new PacketCache();
    }

    @Override
    protected final void apply(
            final Map<Identifier, T>        preparations,
            final @NonNull ResourceManager  manager,
            final @NonNull ProfilerFiller   filler
    ) {
        this.apply(preparations);
        this.cache.encode(this.getStreamCodec(), preparations);
    }

    void handle(@NonNull SyncDatapackPacket packet) {
        final var decode = packet.cache().decode(this.getStreamCodec());
        this.apply(decode);
    }

    protected abstract void apply(@NonNull Map<Identifier, T> preparations);

    protected abstract @NonNull StreamCodec<ByteBuf, Map<Identifier, T>> getStreamCodec();
}
