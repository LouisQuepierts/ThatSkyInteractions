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
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

@Slf4j
@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public final class PlayerInteractionManager extends DataSyncManager<InteractionDefinition> {

    public static final String AUTO = "auto";

    private static final String FOLDER
            = "interaction/definition";

    @Getter
    private static final PlayerInteractionManager instance
            = new PlayerInteractionManager();

    private Map<Identifier, InteractionDefinition> definitions = Map.of();
    private Map<Identifier, PlayerInteraction> interactions = Map.of();

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
        final var definitions       = instance.definitions;

        for (final var entry : definitions.entrySet()) {
            final var identifier    = entry.getKey();
            final var definition    = entry.getValue();

            final var hasLevel      = definition.levels() > 1;
            var level               = 1;

            for (final var interaction : definition.interactions()) {
                final var requester = interaction.requester();
                final var receiver  = interaction.receiver();

                final var id        = hasLevel ?
                                    identifier.withSuffix("_" + level) :
                                    identifier;

                parseRequester(event, id, requester);
                parseReceiver(event, id, receiver);

                level ++;
            }
        }
    }

    public @Nullable PlayerInteraction get(
            @NonNull Identifier     identifier
    ) {
        return this.interactions.get(identifier);
    }

    public @Nullable PlayerInteraction get(
            @NonNull Identifier     identifier,
            int                     level
    ) {
        final var definition    = this.definitions.get(identifier);

        if (definition == null) {
            return null;
        }

        final var hasLevel      = definition.levels() > 1;
        final var id            = hasLevel ? identifier.withSuffix("_" + level) : identifier;
        return this.interactions.get(id);
    }

    public @Nullable InteractionDefinition getDefinition(
            @NonNull Identifier     identifier
    ) {
        return this.definitions.get(identifier);
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
        final var prefix            = identifier.toString();
        final var sources           = Map.of(
                "invite", new SourceDefinition(prefix + ".invite", 0.25f, 0.0f, namespace),
                "waiting", new SourceDefinition(prefix + ".waiting", 0.0f, 0.0f, namespace),
                "cancel", new SourceDefinition(prefix + ".cancel", 0.0f, 0.25f, namespace),
                "main", new SourceDefinition(prefix + ".main", 0.0f, 0.0f, namespace),
                "exit", new SourceDefinition(prefix + ".exit", 0.0f, 0.25f, namespace)
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
        final var prefix            = identifier.toString();
        final var sources           = Map.of(
                "accept", new SourceDefinition(prefix + ".accept", 0.25f, 0.0f, namespace),
                "main", new SourceDefinition(prefix + ".main", 0.0f, 0.0f, namespace),
                "exit", new SourceDefinition(prefix + ".exit", 0.0f, 0.25f, namespace)
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
        final var builder   = ImmutableMap.<Identifier, InteractionDefinition>builderWithExpectedSize(preparations.size());
        final var builder1  = ImmutableMap.<Identifier, PlayerInteraction>builderWithExpectedSize(preparations.size());
        for (final var entry : preparations.entrySet()) {
            final var identifier    = entry.getKey();
            final var definition    = entry.getValue();
            final var hasLevel      = definition.levels() > 1;

            builder.put(identifier, definition);

            if (!hasLevel) {
                builder1.put(
                        identifier,
                        PlayerInteraction.parse(
                                identifier,
                                definition.interactions().getFirst(),
                                1
                        )
                );
            } else {
                int level = 1;
                for (final var interaction : definition.interactions()) {
                    final var id = identifier.withSuffix("_" + level);
                    builder1.put(
                            id,
                            PlayerInteraction.parse(
                                    id,
                                    interaction,
                                    level
                            )
                    );

                    level ++;
                }
            }
        }
        this.definitions    = builder.build();
        this.interactions   = builder1.build();

        log.info("Loaded {} interaction definitions", preparations.size());
        log.info("Loaded {} interaction definitions with level", interactions.size());
    }

    public Iterable<Identifier> identifiers() {
        return this.interactions.keySet();
    }
}
