package net.quepierts.thatskyinteractions.feature.animation.event;

import lombok.RequiredArgsConstructor;
import net.neoforged.bus.api.Event;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerAnimationDefinition;
import net.quepierts.thatskyinteractions.feature.animation.humanoid.PlayerAnimation;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
public final class RegisterPlayerAnimationTypeEvent extends Event {

    private final Map<String, Function<PlayerAnimationDefinition, PlayerAnimation>> factories;

    public void register(
            @NonNull String                                                 type,
            @NonNull Function<PlayerAnimationDefinition, PlayerAnimation>   factory
    ) {
        if (this.factories.put(type, factory) != null) {
            throw new IllegalArgumentException("Animation type already registered: " + type);
        }
    }

}
