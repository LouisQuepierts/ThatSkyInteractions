package net.quepierts.thatskyinteractions.feature.animation.bedrock;

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
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.model.animation.bedrock.BedrockAnimation;
import net.quepierts.thatskyinteractions.core.model.animation.bedrock.BedrockAnimationDefinition;
import net.quepierts.thatskyinteractions.feature.network.PacketCache;
import net.quepierts.thatskyinteractions.feature.network.SyncAnimationSourcePacket;
import org.jspecify.annotations.NonNull;

import java.util.Map;

@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public final class BedrockAnimationManager extends SimpleJsonResourceReloadListener<BedrockAnimationDefinition> {

    public static final ResourceKey<Registry<BedrockAnimationDefinition>> REGISTRY_KEY
            = ResourceKey.createRegistryKey(ThatSkyInteractions.location("animation/source"));

    private static final String FOLDER = "animation/source";

    private static BedrockAnimationManager instance;

    @SubscribeEvent
    public static void onAddReloadListeners(final AddServerReloadListenersEvent event) {
        if (instance != null) {
            instance.cache.free();
        }
        event.addListener(
                REGISTRY_KEY.identifier(),
                instance = new BedrockAnimationManager()
        );
    }

    @SubscribeEvent
    public static void onDatapackSync(final OnDatapackSyncEvent event) {
        if (instance == null || !instance.cache.ready()) {
            return;
        }

        event.getRelevantPlayers().forEach(player -> {
            PacketDistributor.sendToPlayer(
                    player,
                    new SyncAnimationSourcePacket(instance.definitions)
            );
        });
    }

    public static @NonNull BedrockAnimationManager getInstance() {
        if (instance == null) {
            instance = new BedrockAnimationManager();
        }
        return instance;
    }

    private final PacketCache                           cache       = new PacketCache();

    private Map<Identifier, BedrockAnimationDefinition> definitions = Map.of();
    private Map<Identifier, BedrockAnimation>           animations  = Map.of();

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
        this.sync(preparations);
        this.cache.reset(
                new SyncAnimationSourcePacket(this.definitions),
                SyncAnimationSourcePacket.STREAM_CODEC
        );

        NeoForge.EVENT_BUS.post(new BedrockAnimationReloadedEvent(this));
    }

    public BedrockAnimationDefinition getDefinition(Identifier identifier) {
        return this.definitions.get(identifier);
    }

    public BedrockAnimation getAnimation(Identifier identifier) {
        return this.animations.get(identifier);
    }

    public void sync(final Map<Identifier, BedrockAnimationDefinition> definitions) {
        var builder         = ImmutableMap.<Identifier, BedrockAnimationDefinition>builder();
        var builder2        = ImmutableMap.<Identifier, BedrockAnimation>builder();

        for (final var entry : definitions.entrySet()) {
            final var identifier = entry.getKey();
            builder.put(identifier, entry.getValue());

            for (final var entry2 : entry.getValue().animations().entrySet()) {
                builder2.put(
                        identifier.withSuffix("." + entry2.getKey()),
                        entry2.getValue()
                );
            }
        }

        this.definitions    = builder.build();
        this.animations     = builder2.build();
    }

    public record Holder(
            Identifier identifier,
            BedrockAnimationDefinition definition
    ) { }
}
