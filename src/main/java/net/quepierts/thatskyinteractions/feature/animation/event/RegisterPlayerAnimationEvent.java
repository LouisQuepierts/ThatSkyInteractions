package net.quepierts.thatskyinteractions.feature.animation.event;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.Event;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerAnimationDefinition;
import net.quepierts.thatskyinteractions.core.animation.model.SourceDefinition;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Slf4j
@RequiredArgsConstructor
public final class RegisterPlayerAnimationEvent extends Event {

    private final Object2ObjectMap<Identifier, PlayerAnimationDefinition> definitions;

    public void register(
            @NonNull Identifier                 id,
            @NonNull PlayerAnimationDefinition  definition
    ) {
        if (this.definitions.put(id, definition) != null) {
            log.error("Animation already registered: {}", id);
        }
    }

    public void redirect(
            @NonNull Identifier                 id,
            @NonNull PlayerAnimationDefinition  definition
    ) {
        if (this.definitions.put(id, definition) == null) {
            log.error("Animation not registered: {}", id);
        } else {
            log.info("Animation overridden: {}", id);
        }
    }

    public void modify(
            @NonNull Identifier                 id,
            @NonNull String                     name,
            @NonNull SourceDefinition           source
    ) {
        final var definition    = this.definitions.get(id);
        if (definition == null) {
            log.error("Animation not registered: {}", id);
            return;
        }

        try {
            definition.sources().put(name, source);
        } catch (Exception e) {

            final var sources   = new Object2ObjectOpenHashMap<>(definition.sources());
            final var duplicate = new PlayerAnimationDefinition(
                    definition.type(),
                    definition.override(),
                    sources,
                    definition.unlock(),
                    definition.abortable(),
                    definition.restrictMotion()
            );

            this.definitions    .put(id, duplicate);
        }
    }

    public void unregister(
            @NonNull Identifier                 id
    ) {
        if (this.definitions.remove(id) == null) {
            log.error("Animation not registered: {}", id);
        } else {
            log.info("Animation unregistered: {}", id);
        }
    }

    public boolean contains(
            @NonNull Identifier                 id
    ) {
        return this.definitions.containsKey(id);
    }

    public @Nullable PlayerAnimationDefinition get(
            @NonNull Identifier                 id
    ) {
        return this.definitions.get(id);
    }

}
