package net.quepierts.thatskyinteractions.feature.interaction;

import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftAnimationPipeline;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftSkeletonPipeline;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerAnimationDefinition;
import net.quepierts.thatskyinteractions.core.interaction.DefaultInteractionFSM;
import net.quepierts.thatskyinteractions.feature.animation.event.PlayerAnimationControllerEvent;
import net.quepierts.thatskyinteractions.feature.animation.event.RegisterPlayerAnimationTypeEvent;
import net.quepierts.thatskyinteractions.feature.animation.humanoid.PlayerAnimation;
import net.quepierts.thatskyinteractions.feature.animation.humanoid.TemplateAnimation;
import net.quepierts.thatskyinteractions.feature.interaction.packet.ClientboundInteractionControlPacket;
import net.quepierts.veynir.backend.sampler.AnimationSampler;
import net.quepierts.veynir.backend.sampler.SamplingMode;
import net.quepierts.veynir.backend.sampler.WrappedSampler;
import net.quepierts.veynir.core.fsm.FSMParameter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Slf4j
@UtilityClass
@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public class PlayerInteractionHandler {


    @SubscribeEvent
    public static void onPlayerLoggedOut(final PlayerEvent.PlayerLoggedOutEvent event) {
        final var entity = event.getEntity();
        if (entity instanceof ServerPlayer player) {
            PlayerInteractionSystem.cancel(player);
        }
    }

    @SubscribeEvent
    public static void onRegisterAnimationType(final RegisterPlayerAnimationTypeEvent event) {
        event.register(
                PlayerInteractionSystem.ANIMATION_TYPE_REQUESTER,
                PlayerInteractionHandler::requester
        );

        event.register(
                PlayerInteractionSystem.ANIMATION_TYPE_RECEIVER,
                PlayerInteractionHandler::receiver
        );
    }

    @SubscribeEvent
    public static void beforePlayerTick(final PlayerTickEvent.Pre event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        final var attachment    = PlayerInteractionSystem.getInteractionAttachment(player);
        final var ongoing       = attachment.getOngoing();

        if (ongoing == null) {
            return;
        }


        switch (ongoing.getState()) {
            case DONE: {

                attachment.done();
                PacketDistributor.sendToPlayer(player, ClientboundInteractionControlPacket.done());

                final var uuid  = ongoing.getOther();
                final var other = player.level().getPlayerByUUID(uuid);

                if (other instanceof ServerPlayer receiver) {
                    PlayerInteractionSystem.getInteractionAttachment(receiver).done();
                    PacketDistributor.sendToPlayer(receiver, ClientboundInteractionControlPacket.done());
                    return;
                }

                break;
            }
            case WAITING: {

                if (ongoing.isRequester()) {
                    final var time = player.level().getGameTime();

                    if (ongoing.isExpired(time)) {

                        PlayerInteractionSystem.cancel(player);

                    }
                }

                break;
            }
            case RUNNING: {

                final var interaction = ongoing.getInteraction().get();
                if (interaction != null && interaction.shouldFinish(player)) {
                    ongoing.done();
                }

                break;
            }
        }

    }

    @SubscribeEvent
    public static void onAnimationFinished(final PlayerAnimationControllerEvent.Finished event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        final var attachment    = PlayerInteractionAttachment.getAttachment(player);
        final var ongoing       = attachment.getOngoing();
        if (ongoing == null) {
            return;
        }

        final var interaction = ongoing.getInteraction().get();

        if (interaction != null) {
            interaction.onAnimationFinished(player, event);
        }

    }

    private static @NonNull PlayerAnimation requester(
            final @NonNull PlayerAnimationDefinition definition
    ) {

        final var context   = TemplateAnimation.ParsingContext.of(
                DefaultInteractionFSM.REQUESTER,
                DefaultMinecraftAnimationPipeline.HUMANOID_TIMELINE,
                DefaultMinecraftSkeletonPipeline.MODIFIED_HUMANOID,
                DefaultInteractionFSM.REQUESTER_STATES,
                TemplateAnimation.Constructor.DEFAULT,
                PlayerInteractionHandler::fallback
        );

        final var template  = TemplateAnimation.template(
                definition,
                context
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
            final @NonNull PlayerAnimationDefinition definition
    ) {

        final var context   = TemplateAnimation.ParsingContext.of(
                DefaultInteractionFSM.RECEIVER,
                DefaultMinecraftAnimationPipeline.HUMANOID_TIMELINE,
                DefaultMinecraftSkeletonPipeline.MODIFIED_HUMANOID,
                DefaultInteractionFSM.RECEIVER_STATES,
                TemplateAnimation.Constructor.DEFAULT,
                PlayerInteractionHandler::fallback
        );

        final var template  = TemplateAnimation.template(
                definition,
                context
        );

        template.setFrozenEnd(DefaultInteractionFSM.RECEIVER_EXIT, true);

        return template;
    }

    private static @Nullable AnimationSampler fallback(
            final @NonNull Int2ObjectFunction<AnimationSampler>     getter,
            final @NonNull FSMParameter                             fsmParameter,
            final           String                                  name,
            final           int                                     index
    ) {

        switch (name) {
            case "cancel":
            case "exit": {

                final var last                  = getter.get(index - 2);
                fsmParameter.duration()[index]  = 0.25f;
                fsmParameter.fadeIn()[index]    = 0.0f;
                fsmParameter.fadeOut()[index]   = 0.25f;
                return                          WrappedSampler.wrap(last, SamplingMode.FREEZE_END);

            }

            case "accept": {

                final var next                  = getter.get(index);
                fsmParameter.duration()[index]  = 0.25f;
                fsmParameter.fadeIn()[index]    = 0.25f;
                fsmParameter.fadeOut()[index]   = 0.0f;
                return                          WrappedSampler.wrap(next, SamplingMode.FREEZE_START);

            }
        }

        return null;
    }

}
