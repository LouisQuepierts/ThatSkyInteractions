package net.quepierts.thatskyinteractions.feature.data;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.data.packet.PacketCache;
import net.quepierts.thatskyinteractions.feature.data.packet.SyncDatapackPacket;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.function.IntFunction;

public abstract class DataSyncManager<T> extends SimpleJsonResourceReloadListener<T> {

    @Getter
    private final Identifier identifier;

    @Getter
    private final PacketCache cache;

    @Getter
    private final StreamCodec<? super RegistryFriendlyByteBuf, Map<Identifier, T>> streamCodec;

    protected DataSyncManager(
            final Codec<T>                                          codec,
            final StreamCodec<? super RegistryFriendlyByteBuf, T>   streamCodec,
            final String                                            folder
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
        final Map<Identifier, T> payload = this.onHostLoaded(preparations);
        this.apply(payload);

        this.cache.cache(
                this.getStreamCodec(),
                payload
        );
    }

    void handle(@NonNull SyncDatapackPacket packet) {
        final var decode = packet.cache().decode(this.getStreamCodec());
        this.apply(decode);
        this.onSynced();
    }

    protected abstract void apply(@NonNull Map<Identifier, T> preparations);

    protected @NonNull Map<Identifier, T> onHostLoaded(@NonNull Map<Identifier, T> preparations) {
        return preparations;
    }

    protected void onSynced() { }

    protected static <T> StreamCodec<? super RegistryFriendlyByteBuf, Map<Identifier, T>> createStreamCodec(
            @NonNull final StreamCodec<? super RegistryFriendlyByteBuf, T> element
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
