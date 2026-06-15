package net.quepierts.thatskyinteractions.feature.animation;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerAnimationDefinition;
import net.quepierts.thatskyinteractions.feature.animation.event.RegisterPlayerAnimationEvent;
import net.quepierts.thatskyinteractions.feature.animation.humanoid.PlayerAnimation;
import net.quepierts.thatskyinteractions.feature.data.DataSyncManager;
import net.quepierts.thatskyinteractions.feature.data.event.RegisterSyncManagerEvent;
import org.jspecify.annotations.NonNull;

import java.util.Map;

@Slf4j
@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public final class PlayerAnimationManager extends DataSyncManager<PlayerAnimationDefinition> {

    private static final String FOLDER
            = "animation/definition";

    @Getter
    private static final PlayerAnimationManager instance
            = new PlayerAnimationManager();

    private Map<Identifier, Holder> map = Map.of();

    PlayerAnimationManager() {
        super(
                PlayerAnimationParser.ANIMATION_CODEC,
                PlayerAnimationParser.ANIMATION_STREAM_CODEC,
                FOLDER
        );
    }

    @SubscribeEvent
    public static void onRegisterSyncManager(final RegisterSyncManagerEvent event) {
        event.register(instance);
    }

    @Override
    protected void apply(final @NonNull Map<Identifier, PlayerAnimationDefinition> preparations) {
        var builder = ImmutableMap.<Identifier, Holder>builder();
        for (var entry : preparations.entrySet()) {
            var id = entry.getKey();
            var definition = entry.getValue();
            builder.put(id, new Holder(definition));
        }
        this.map = builder.build();

        log.info("Loaded {} player animations", this.map.size());
    }

    @Override
    protected @NonNull Map<Identifier, PlayerAnimationDefinition> onHostLoaded(final @NonNull Map<Identifier, PlayerAnimationDefinition> preparations) {

        final var modified  = new Object2ObjectOpenHashMap<>(preparations);
        final var event     = new RegisterPlayerAnimationEvent(modified);

        NeoForge.EVENT_BUS  .post(event);

        return modified;
    }

    public PlayerAnimation get(Identifier id) {
        var holder = this.map.get(id);
        if (holder == null) {
            log.error("Animation not found: {}", id);
            return null;
        }
        return holder.get(id);
    }

    public PlayerAnimationDefinition getDefinition(Identifier id) {
        var holder = this.map.get(id);
        if (holder == null) {
            log.error("Animation not found: {}", id);
            return null;
        }
        return holder.definition;
    }

    public Iterable<Identifier> identifiers() {
        return this.map.keySet();
    }


    @RequiredArgsConstructor
    private static final class Holder {

        private final PlayerAnimationDefinition definition;
        private PlayerAnimation                 animation;
        private boolean                         initialized = false;

        public PlayerAnimation get(final Identifier id) {
            if (!this.initialized) {
                try {
                    this.initialize();
                } catch (Exception e) {
                    log.error("Failed to initialize animation: {}", id);
                }
            }

            return this.animation;
        }

        private void initialize() {
            this.initialized    = true;
            this.animation      = PlayerAnimationFactory.create(this.definition);
        }

    }
}
