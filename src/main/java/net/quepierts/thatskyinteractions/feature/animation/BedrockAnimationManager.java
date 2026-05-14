package net.quepierts.thatskyinteractions.feature.animation;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Registry;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.model.animation.BedrockAnimationDefinition;
import org.jspecify.annotations.NonNull;

import java.util.Map;

@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public final class BedrockAnimationManager extends SimpleJsonResourceReloadListener<BedrockAnimationDefinition> {

    public static final ResourceKey<Registry<BedrockAnimationDefinition>> REGISTRY_KEY
            = ResourceKey.createRegistryKey(ThatSkyInteractions.location("animations"));

    private static final String FOLDER = "animations";

    private static BedrockAnimationManager instance;

    @SubscribeEvent
    public static void onAddReloadListeners(final AddServerReloadListenersEvent event) {
        event.addListener(
                REGISTRY_KEY.identifier(),
                instance = new BedrockAnimationManager()
        );
    }

    public static @NonNull BedrockAnimationManager getInstance() {
        if (instance == null)
            throw new IllegalStateException("BedrockAnimationManager is not initialized");
        return instance;
    }

    private Map<Identifier, Holder> animations = Map.of();

    private BedrockAnimationManager() {
        super(
                BedrockAnimationParser.ANIMATION_DEFINITION_CODEC,
                FileToIdConverter.json(FOLDER)
        );

    }

    @Override
    protected void apply(
            final           Map<Identifier, BedrockAnimationDefinition> preparations,
            final @NonNull  ResourceManager                             manager,
            final @NonNull  ProfilerFiller                              profiler
    ) {
        var builder         = ImmutableMap.<Identifier, Holder>builder();
        preparations        .forEach(
                                (id, def)
                                    -> builder.put(id, new Holder(id, def))
        );

        this.animations     = builder.buildOrThrow();
    }

    public Holder get(Identifier identifier) {
        return this.animations.get(identifier);
    }

    public record Holder(
            Identifier identifier,
            BedrockAnimationDefinition definition
    ) { }
}
