package net.quepierts.thatskyinteractions.feature.interaction;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftAnimationPipeline;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftSkeletonPipeline;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerAnimationDefinition;
import net.quepierts.thatskyinteractions.core.interaction.DefaultInteractionFSM;
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
                PlayerInteractionSystem::requester
        );

        event.register(
                ANIMATION_TYPE_RECEIVER,
                PlayerInteractionSystem::receiver
        );
    }

    private static @NonNull PlayerAnimation requester(
            @NonNull final PlayerAnimationDefinition definition
    ) {

        final var template  = TemplateAnimation.template(
                definition,
                DefaultInteractionFSM.REQUESTER,
                DefaultMinecraftAnimationPipeline.HUMANOID_TIMELINE,
                DefaultMinecraftSkeletonPipeline.MODIFIED_HUMANOID
        );

        template.setFrozenEnd(DefaultInteractionFSM.REQUESTER_CANCEL, true);
        template.setFrozenEnd(DefaultInteractionFSM.REQUESTER_EXIT, true);

        template.setExitPoint(
                DefaultInteractionFSM.REQUESTER_WAITING,
                DefaultInteractionFSM.REQUESTER_CANCEL
        );

        return template;
    }

    private static @NonNull PlayerAnimation receiver(
            @NonNull final PlayerAnimationDefinition definition
    ) {

        final var template  = TemplateAnimation.template(
                definition,
                DefaultInteractionFSM.RECEIVER,
                DefaultMinecraftAnimationPipeline.HUMANOID_TIMELINE,
                DefaultMinecraftSkeletonPipeline.MODIFIED_HUMANOID
        );

        template.setFrozenEnd(DefaultInteractionFSM.RECEIVER_EXIT, true);

        return template;
    }

}
