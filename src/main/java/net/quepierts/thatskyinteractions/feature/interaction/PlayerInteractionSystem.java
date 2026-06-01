package net.quepierts.thatskyinteractions.feature.interaction;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerAnimationDefinition;
import net.quepierts.thatskyinteractions.feature.animation.event.RegisterPlayerAnimationTypeEvent;
import net.quepierts.thatskyinteractions.feature.animation.humanoid.PlayerAnimation;
import net.quepierts.thatskyinteractions.feature.animation.humanoid.TemplateAnimation;
import org.jspecify.annotations.NonNull;

@Slf4j
@UtilityClass
@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public class PlayerInteractionSystem {

    public static final String ANIMATION_TYPE_REQUESTER = "interaction.requester";
    public static final String ANIMATION_TYPE_RECEIVER  = "interaction.receiver";

    @SubscribeEvent
    public static void onPlayerLoggedOut(final PlayerEvent.PlayerLoggedOutEvent event) {
        // test
        final var entity = event.getEntity();
        log.info("Player {} logged out, id {}", entity.getName().getString(), entity.getId());
    }

    @SubscribeEvent
    public static void onRegisterAnimationType(final RegisterPlayerAnimationTypeEvent event) {
        event.register(
                ANIMATION_TYPE_REQUESTER,
                PlayerInteractionSystem::typeRequester
        );

        event.register(
                ANIMATION_TYPE_RECEIVER,
                PlayerInteractionSystem::typeReceiver
        );
    }

    private static @NonNull PlayerAnimation typeRequester(
            @NonNull final PlayerAnimationDefinition definition
    ) {
        throw new UnsupportedOperationException();
    }

    private static @NonNull PlayerAnimation typeReceiver(
            @NonNull final PlayerAnimationDefinition definition
    ) {
        throw new UnsupportedOperationException();
    }

}
