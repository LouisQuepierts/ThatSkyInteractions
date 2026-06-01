package net.quepierts.thatskyinteractions.feature.interaction;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerAnimationDefinition;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerMask;
import net.quepierts.thatskyinteractions.core.animation.model.SourceDefinition;
import net.quepierts.thatskyinteractions.core.interaction.model.InteractionDefinition;
import net.quepierts.thatskyinteractions.feature.animation.event.RegisterPlayerAnimationEvent;
import net.quepierts.thatskyinteractions.feature.data.DataSyncManager;
import net.quepierts.thatskyinteractions.feature.data.event.RegisterSyncManagerEvent;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.Optional;

@Slf4j
@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public final class PlayerInteractionManager extends DataSyncManager<InteractionDefinition> {

    private static final String AUTO = "auto";

    private static final String FOLDER
            = "interaction/definition";

    @Getter
    private static final PlayerInteractionManager instance
            = new PlayerInteractionManager();

    private Map<Identifier, InteractionDefinition> map = Map.of();

    PlayerInteractionManager() {
        super(
                PlayerInteractionParser.DEFINITION_CODEC,
                PlayerInteractionParser.DEFINITION_STREAM_CODEC,
                FOLDER
        );
    }

    @SubscribeEvent
    public static void onRegisterSyncManager(final RegisterSyncManagerEvent event) {
        event.registerBefore(instance, ThatSkyInteractions.location("animation/definition"));
    }

    @SubscribeEvent
    public static void onRegisterPlayerAnimation(final RegisterPlayerAnimationEvent event) {
        final var definitions       = instance.map;

        for (final var entry : definitions.entrySet()) {
            final var identifier    = entry.getKey();
            final var definition    = entry.getValue();

            for (final var interaction : definition.interactions()) {
                final var requester = interaction.requester();
                final var receiver  = interaction.receiver();

                parseRequester(event, identifier, requester);
                parseReceiver(event, identifier, receiver);
            }
        }
    }

    private static void parseRequester(
            RegisterPlayerAnimationEvent    event,
            Identifier                      identifier,
            String                          source
    ) {

        if (!AUTO.equals(source)) {

            final var id            = Identifier.parse(source);
            final var definition    = event.get(id);

            if (definition == null) {
                log.warn("Interaction source {} not found", id);
            }

            return;
        }

        final var namespace         = Optional.of("requester");
        final var sources           = Map.of(
                "inviting", new SourceDefinition("inviting", 0.25f, 0.0f, namespace),
                "waiting", new SourceDefinition("waiting", 0.0f, 0.0f, namespace),
                "cancel", new SourceDefinition("cancel", 0.0f, 0.25f, namespace),
                "main", new SourceDefinition("main", 0.0f, 0.0f, namespace),
                "exit", new SourceDefinition("exit", 0.0f, 0.25f, namespace)
        );

        final var definition        = new PlayerAnimationDefinition(
                PlayerInteractionSystem.ANIMATION_TYPE_REQUESTER,
                "thatskyinteractions:modified",
                sources,
                PlayerMask.EMPTY,
                false,
                true,
                false
        );

        final var id                = identifier.withSuffix(".requester");
        event.register(id, definition);
    }

    private static void parseReceiver(
            RegisterPlayerAnimationEvent    event,
            Identifier                      identifier,
            String                          source
    ) {
        if (!AUTO.equals(source)) {

            final var id            = Identifier.parse(source);
            final var definition    = event.get(id);

            if (definition == null) {
                log.warn("Interaction source {} not found", id);
            }

            return;
        }

        final var namespace         = Optional.of("receiver");
        final var sources           = Map.of(
                "accept", new SourceDefinition("accept", 0.25f, 0.0f, namespace),
                "main", new SourceDefinition("main", 0.0f, 0.0f, namespace),
                "exit", new SourceDefinition("exit", 0.0f, 0.25f, namespace)
        );

        final var definition        = new PlayerAnimationDefinition(
                PlayerInteractionSystem.ANIMATION_TYPE_RECEIVER,
                "thatskyinteractions:modified",
                sources,
                PlayerMask.EMPTY,
                false,
                true,
                false
        );

        final var id                = identifier.withSuffix(".receiver");
        event.register(id, definition);
    }

    @Override
    protected void apply(@NonNull final Map<Identifier, InteractionDefinition> preparations) {
        final var builder = ImmutableMap.<Identifier, InteractionDefinition>builderWithExpectedSize(preparations.size());
        preparations.forEach(builder::put);
        this.map = builder.build();

        log.info("Loaded {} interaction definitions", preparations.size());
    }
}
