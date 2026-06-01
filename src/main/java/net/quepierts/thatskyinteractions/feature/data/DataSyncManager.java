package net.quepierts.thatskyinteractions.feature.data;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.network.PacketCache;
import net.quepierts.thatskyinteractions.feature.network.SyncDatapackPacket;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.function.IntFunction;

public abstract class DataSyncManager<T> extends SimpleJsonResourceReloadListener<T> {

    @Getter
    private final Identifier identifier;

    @Getter
    private final PacketCache cache;

    @Getter
    private final StreamCodec<ByteBuf, Map<Identifier, T>> streamCodec;

    protected DataSyncManager(
            final Codec<T>                  codec,
            final StreamCodec<ByteBuf, T>   streamCodec,
            final String                    folder
    ) {
        super(
                codec,
                FileToIdConverter.json(folder)
        );
        this.identifier     = ThatSkyInteractions.location(folder);
        this.cache          = new PacketCache();

        this.streamCodec    = createStreamCodec(streamCodec);
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

    protected static <T> StreamCodec<ByteBuf, Map<Identifier, T>> createStreamCodec(
            @NonNull final StreamCodec<ByteBuf, T> element
    ) {
        return ByteBufCodecs.map(
                (IntFunction<Map<Identifier, T>>) Object2ObjectOpenHashMap::new,
                ByteBufCodecs.STRING_UTF8.map(
                        Identifier::parse,
                        Identifier::toString
                ),
                element
        );
    }
}
