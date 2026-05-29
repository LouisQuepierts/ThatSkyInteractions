package net.quepierts.thatskyinteractions.feature.animation;

import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.animation.model.ParentOverrideDefinition;
import net.quepierts.thatskyinteractions.feature.data.DataSyncManager;
import net.quepierts.thatskyinteractions.feature.data.DataSyncSystem;
import net.quepierts.animata4j.backend.skeleton.SkeletonLayout;
import net.quepierts.animata4j.core.model.ParentOverrideConfiguration;
import org.jspecify.annotations.NonNull;

import java.util.Map;

@Slf4j
public final class ParentOverrideManager extends DataSyncManager<ParentOverrideDefinition> {

    public static final Identifier DISABLED
            = ThatSkyInteractions.location("disabled");
    
    public static final String FOLDER
            = "animation/override";
    
    public static final StreamCodec<ByteBuf, Map<Identifier, ParentOverrideDefinition>> STREAM_CODEC 
            = createStreamCodec(ParentOverrideParser.STREAM_CODEC);
    
    private static final ParentOverrideManager instance 
            = DataSyncSystem.register(ParentOverrideManager::new);
    
    public static @NonNull ParentOverrideManager getInstance() {
        return instance;
    }
    
    private Map<Identifier, Holder> map = Map.of();

    ParentOverrideManager() {
        super(
                ParentOverrideParser.CODEC,
                FOLDER
        );
    }

    @Override
    protected void apply(@NonNull final Map<Identifier, ParentOverrideDefinition> preparations) {
        var builder = ImmutableMap.<Identifier, Holder>builder();
        for (var entry : preparations.entrySet()) {
            var id = entry.getKey();
            var definition = entry.getValue();
            
            builder.put(id, new Holder(definition));
        }
        this.map = builder.build();

        log.info("Loaded {} override definitions", preparations.size());
    }
    
    public ParentOverrideConfiguration get(
            @NonNull final Identifier       identifier,
            @NonNull final SkeletonLayout   layout
    ) {

        if (identifier.equals(DISABLED)) {
            return null;
        }

        var holder = this.map.get(identifier);
        
        if (holder == null) {
            log.error("Override Definition not found: {}", identifier);
            return null;
        }

        return holder.get(layout);
    }

    @Override
    protected @NonNull StreamCodec<ByteBuf, Map<Identifier, ParentOverrideDefinition>> getStreamCodec() {
        return STREAM_CODEC;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Holder {
        private final Map<SkeletonLayout, ParentOverrideConfiguration> configurations = new Object2ObjectOpenHashMap<>();
        private final ParentOverrideDefinition definition;

        public ParentOverrideConfiguration get(final @NonNull SkeletonLayout layout) {
            final var configuration = this.configurations.get(layout);
            if (configuration != null) {
                return configuration;
            }

            final var fresh         = ParentOverrideParser.parse(
                                        this.definition,
                                        layout
            );
            this.configurations     .put(layout, fresh);
            return fresh;
        }
    }
    
}
