package net.quepierts.thatskyinteractions.feature.animation.binary;

import com.google.common.collect.ImmutableMap;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.quepierts.veynir.backend.source.AnimationSource;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipInputStream;

// todo: use compiled source insteadof raw source
@Slf4j
public final class AnimationSourceManager extends SimplePreparableReloadListener<Map<Identifier, AnimationSource>> {

    public static final String FOLDER                   = "animation/binary";
    private static final FileToIdConverter LISTER       = new FileToIdConverter(FOLDER, ".anim.bin");

    private Map<Identifier, AnimationSource> sources    = Map.of();

    @Override
    protected Map<Identifier, AnimationSource> prepare(
            final @NonNull ResourceManager                      manager,
            final @NonNull ProfilerFiller                       filler
    ) {
        final var lister = LISTER;

        final var map       = new HashMap<Identifier, AnimationSource>();

        for (final var entry : lister.listMatchingResources(manager).entrySet()) {
            final var location  = entry.getKey();
            final var id        = lister.fileToId(location);

            try (   final var rin    = entry.getValue().open();
                    final var zin    = new ZipInputStream(rin)
            ) {

                zin.getNextEntry();
                final var bytes     = zin.readAllBytes();
                final var buffer    = Unpooled.wrappedBuffer(bytes);
                final var decoded   = SourceParser.SOURCE.decode(buffer);

                map.put(id, decoded);

                buffer.release();

            } catch (Exception e) {
                log.error("Failed to load animation source: {}", id, e);
            }
        }

        return map;
    }

    @Override
    protected void apply(
            final @NonNull Map<Identifier, AnimationSource>     preparations,
            final @NonNull ResourceManager                      manager,
            final @NonNull ProfilerFiller                       filler
    ) {
        this.sources = ImmutableMap.copyOf(preparations);
    }
}
