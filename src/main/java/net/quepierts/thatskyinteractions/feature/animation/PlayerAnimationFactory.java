package net.quepierts.thatskyinteractions.feature.animation;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.neoforged.neoforge.common.NeoForge;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerAnimationDefinition;
import net.quepierts.thatskyinteractions.feature.animation.event.RegisterPlayerAnimationTypeEvent;
import net.quepierts.thatskyinteractions.feature.animation.humanoid.PlayerAnimation;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

@Slf4j
@UtilityClass
public class PlayerAnimationFactory {

    private static final Map<String, Function<PlayerAnimationDefinition, PlayerAnimation>> FACTORIES
            = new Object2ObjectOpenHashMap<>();

    private static boolean initialized = false;

    public static @Nullable PlayerAnimation create(@NonNull PlayerAnimationDefinition definition) {
        final var type      = definition.type();
        final var factory   = FACTORIES.get(type);

        if (factory == null) {
            log.error("Unknown animation type: {}", type);
            return null;
        }

        return factory.apply(definition);
    }

    public static void register() {
        if (initialized) {
            return;
        }

        initialized = true;

        FACTORIES.put("simple", PlayerAnimation::simple);
        FACTORIES.put("sequence", PlayerAnimation::sequence);

        final var event = new RegisterPlayerAnimationTypeEvent(FACTORIES);
        NeoForge.EVENT_BUS.post(event);
    }

}
