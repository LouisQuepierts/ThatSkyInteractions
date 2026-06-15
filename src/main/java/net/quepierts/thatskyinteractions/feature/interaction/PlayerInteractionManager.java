package net.quepierts.thatskyinteractions.feature.interaction;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.animation.event.RegisterPlayerAnimationEvent;
import net.quepierts.thatskyinteractions.feature.data.DataSyncManager;
import net.quepierts.thatskyinteractions.feature.data.event.RegisterSyncManagerEvent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;

@Slf4j
@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public final class PlayerInteractionManager extends DataSyncManager<InteractionSet> {

    public static final String AUTO = "auto";

    private static final String FOLDER
            = "interaction/definition";

    @Getter
    private static final PlayerInteractionManager instance
            = new PlayerInteractionManager();

    private Map<Identifier, InteractionSet> sets = Map.of();
    private Map<Identifier, Interaction> interactions = Map.of();

    PlayerInteractionManager() {
        super(
                InteractionSet.CODEC,
                InteractionSet.STREAM_CODEC,
                FOLDER
        );
    }

    @SubscribeEvent
    public static void onRegisterSyncManager(final RegisterSyncManagerEvent event) {
        event.registerBefore(instance, ThatSkyInteractions.location("animation/definition"));
    }

    @SubscribeEvent
    public static void onRegisterPlayerAnimation(final RegisterPlayerAnimationEvent event) {
        /*final var definitions       = instance.definitions;

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
        }*/

        for (final var entry : instance.sets.entrySet()) {

            final var key       = entry.getKey();
            final var value     = entry.getValue();

            final var leveled   = value.leveled();

            var level           = leveled ? 1 : 0;
            for (final var interaction : value.interactions()) {

                interaction.onRegisterPlayerAnimation(
                        event,
                        key,
                        level
                );

                level ++;

            }

        }
    }



    public @Nullable Interaction get(
            @NonNull Identifier     identifier
    ) {
        return this.interactions.get(identifier);
    }

    public @Nullable Interaction get(
            @NonNull Identifier     identifier,
            int                     level
    ) {
        final var set               = this.sets.get(identifier);
        return set != null ? set.interactions().get(level - 1) : null;
    }

    @Override
    protected void apply(final @NonNull Map<Identifier, InteractionSet> preparations) {

        final var builder0  = ImmutableMap.<Identifier, InteractionSet>builder();
        final var builder1  = ImmutableMap.<Identifier, Interaction>builder();

        for (final var entry : preparations.entrySet()) {
            final var identifier    = entry.getKey();
            final var set           = entry.getValue();
            final var leveled       = set.leveled();

            builder0.put(identifier, set);

            if (!leveled) {
                final var first = set.interactions().getFirst();
                first.onGenerateData(
                        identifier,
                        0
                );
                builder1.put(
                        identifier,
                        first
                );
            } else {
                int level = 1;
                for (final var interaction : set.interactions()) {
                    interaction.onGenerateData(
                            identifier,
                            level
                    );

                    final var id = identifier.withSuffix("_" + level);
                    builder1.put(
                            id,
                            interaction
                    );

                    level ++;
                }
            }
        }

        this.sets           = builder0.build();
        this.interactions   = builder1.build();

        log.info("Loaded {} interaction sets", this.sets.size());
        log.info("Loaded {} interaction", this.interactions.size());
    }

    public Iterable<Identifier> identifiers() {
        return this.interactions.keySet();
    }
}
