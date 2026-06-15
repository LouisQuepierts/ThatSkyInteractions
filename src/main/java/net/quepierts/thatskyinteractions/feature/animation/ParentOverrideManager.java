package net.quepierts.thatskyinteractions.feature.animation;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.animation.model.ParentOverrideDefinition;
import net.quepierts.thatskyinteractions.feature.data.DataSyncManager;
import net.quepierts.veynir.backend.skeleton.SkeletonLayout;
import net.quepierts.veynir.core.model.ParentOverrideConfiguration;
import net.quepierts.thatskyinteractions.feature.data.event.RegisterSyncManagerEvent;
import org.jspecify.annotations.NonNull;

import java.util.Map;

@Slf4j
@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public final class ParentOverrideManager extends DataSyncManager<ParentOverrideDefinition> {

    public static final Identifier DISABLED
            = ThatSkyInteractions.location("disabled");
    
    public static final String FOLDER
            = "animation/override";

    @Getter
    private static final ParentOverrideManager instance 
            = new ParentOverrideManager();
    
    private Map<Identifier, Holder> map = Map.of();

    ParentOverrideManager() {
        super(
                ParentOverrideParser.CODEC,
                ParentOverrideParser.STREAM_CODEC,
                FOLDER
        );
    }

    @SubscribeEvent
    public static void onRegisterSyncManager(final RegisterSyncManagerEvent event) {
        event.register(instance);
    }

    @Override
    protected void apply(final @NonNull Map<Identifier, ParentOverrideDefinition> preparations) {
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
            final @NonNull Identifier       identifier,
            final @NonNull SkeletonLayout   layout
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
