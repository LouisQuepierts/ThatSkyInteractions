package net.quepierts.thatskyinteractions.feature.animation;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.model.animation.PlayerAnimationDefinition;
import net.quepierts.thatskyinteractions.feature.animation.humanoid.PlayerAnimation;
import net.quepierts.thatskyinteractions.feature.network.SyncAnimationDefinitionPacket;
import org.jspecify.annotations.NonNull;

import java.util.Map;

@Slf4j
@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public final class PlayerAnimationManager extends SimpleJsonResourceReloadListener<PlayerAnimationDefinition> {

    public static final ResourceKey<Registry<PlayerAnimationDefinition>> REGISTRY_KEY
            = ResourceKey.createRegistryKey(ThatSkyInteractions.location("animation/definition"));

    private static final String FOLDER = "animation/definition";

    private static PlayerAnimationManager instance;

    @SubscribeEvent
    public static void onAddReloadListeners(final AddServerReloadListenersEvent event) {
        event.addListener(
                REGISTRY_KEY.identifier(),
                instance = new PlayerAnimationManager()
        );
    }

    @SubscribeEvent
    public static void onDatapackSync(final OnDatapackSyncEvent event) {
        if (instance == null) {
            return;
        }

        event.getRelevantPlayers().forEach(player -> {
            PacketDistributor.sendToPlayer(
                    player,
                    new SyncAnimationDefinitionPacket(instance.definitions)
            );
        });
    }

    public static @NonNull PlayerAnimationManager getInstance() {
        if (instance == null) {
            instance = new PlayerAnimationManager();
        }
        return instance;
    }

    private Map<Identifier, PlayerAnimationDefinition> definitions = Map.of();
    private Map<Identifier, Holder> map = Map.of();

    PlayerAnimationManager() {
        super(
                PlayerAnimationParser.ANIMATION_CODEC,
                FileToIdConverter.json(FOLDER)
        );
    }

    @Override
    protected void apply(
            final           Map<Identifier, PlayerAnimationDefinition>        preparations,
            final @NonNull  ResourceManager                             manager,
            final @NonNull  ProfilerFiller                              profiler
    ) {
        this.sync(preparations);
    }

    public PlayerAnimation get(Identifier id) {
        var holder = this.map.get(id);
        if (holder == null) {
            log.error("Animation not found: {}", id);
            return null;
        }
        return holder.get();
    }

    public Iterable<Identifier> identifiers() {
        return this.map.keySet();
    }

    public void sync(final Map<Identifier, PlayerAnimationDefinition> definitions) {
        var builder = ImmutableMap.<Identifier, Holder>builder();
        var builder2 = ImmutableMap.<Identifier, PlayerAnimationDefinition>builder();
        for (var entry : definitions.entrySet()) {
            var id = entry.getKey();
            var definition = entry.getValue();
            builder.put(id, new Holder(definition));
            builder2.put(id, definition);
        }
        this.map = builder.build();
        this.definitions = builder2.build();
    }

    @RequiredArgsConstructor
    private static final class Holder {

        private final PlayerAnimationDefinition definition;
        private PlayerAnimation             animation;

        public PlayerAnimation get() {
            if (this.animation == null) {
                this.initialize();
            }

            return this.animation;
        }

        private void initialize() {

            switch (this.definition.type()) {
                case "simple":
                    this.animation = PlayerAnimation.simple(this.definition);
                    break;
                case "sequence":
                    this.animation = PlayerAnimation.sequence(this.definition);
                    break;
                default:
                    log.error("Unknown animation type: {}", this.definition.type());
            }
        }

    }
}
