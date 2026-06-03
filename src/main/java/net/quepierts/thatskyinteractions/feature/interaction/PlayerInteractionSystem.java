package net.quepierts.thatskyinteractions.feature.interaction;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftAnimationPipeline;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftSkeletonPipeline;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerAnimationDefinition;
import net.quepierts.thatskyinteractions.core.interaction.DefaultInteractionFSM;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.animation.event.RegisterPlayerAnimationTypeEvent;
import net.quepierts.thatskyinteractions.feature.animation.humanoid.PlayerAnimation;
import net.quepierts.thatskyinteractions.feature.animation.humanoid.TemplateAnimation;
import net.quepierts.thatskyinteractions.feature.interaction.event.PlayerInteractionEvent;
import net.quepierts.thatskyinteractions.feature.interaction.packet.InteractionControlPacket;
import net.quepierts.thatskyinteractions.feature.registry.AttachmentTypes;
import net.quepierts.thatskyinteractions.feature.utils.PlayerUtils;
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

    public static PlayerInteractionData getInteractionData(
            @NonNull final Entity entity
    ) {
        return entity.getData(AttachmentTypes.PLAYER_INTERACTION);
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

    public static void invite(
            final @NonNull ServerPlayer requester,
            final @NonNull ServerPlayer receiver,
            final @NonNull Identifier   interaction
    ) {

        if (requester.is(receiver)) {
            return;
        }

        final var manager       = PlayerInteractionManager.getInstance();
        final var definition    = manager.get(interaction);

        if (definition == null) {
            return;
        }

        final ICancellableEvent event = NeoForge.EVENT_BUS.post(new PlayerInteractionEvent.Invite.Pre(requester, receiver, interaction));
        if (event.isCanceled()) {
            return;
        }

        final var level = requester.level();
        if (level != receiver.level()) {
            return;
        }

        if (requester.distanceToSqr(receiver) > 256) {
            return;
        }

        final var reqData = PlayerInteractionSystem.getInteractionData(requester);
        final var recData = PlayerInteractionSystem.getInteractionData(receiver);

        if (reqData.hasSentRequest()) {
            PlayerInteractionSystem.cancel(requester);
        }

        reqData.sendInvite(receiver, interaction);
        recData.receiveInvite(requester, interaction);

        PlayerAnimationSystem.play(
                requester,
                definition.requester()
        );

        PacketDistributor.sendToPlayer(
                requester,
                InteractionControlPacket.invite(receiver, interaction, true)
        );

        PacketDistributor.sendToPlayer(
                receiver,
                InteractionControlPacket.invite(requester, interaction, false)
        );

        NeoForge.EVENT_BUS.post(new PlayerInteractionEvent.Invite.Post(requester, receiver, interaction));

    }

    public static void accept(
            final @NonNull ServerPlayer requester,
            final @NonNull ServerPlayer receiver,
            final boolean               force
    ) {

        if (requester.is(receiver)) {
            return;
        }

        final var reqData = PlayerInteractionSystem.getInteractionData(requester);
        final var recData = PlayerInteractionSystem.getInteractionData(receiver);

        final var sent = reqData.getSent();
        if (sent == null || !sent.getOther().equals(receiver.getUUID())) {
            return;
        }

        if (!recData.hasReceivedRequest(requester.getUUID())) {
            return;
        }

        final var level = requester.level();
        if (level != receiver.level()) {
            return;
        }

        final var type      = sent.getType();
        final var manager   = PlayerInteractionManager.getInstance();

        final var interaction = manager.get(type);

        if (interaction == null) {
            return;
        }

        final var event     = NeoForge.EVENT_BUS.post(new PlayerInteractionEvent.Accept.Pre(requester, receiver));
        if (event.isCanceled()) {
            return;
        }

        final var position = PlayerUtils.getRelativePositionWorldSpace(requester, 1.0, 0.0);

        if (!force && receiver.distanceToSqr(position) > 0.1) {
            return;
        }

        receiver.setPos(position);
        receiver.lookAt(EntityAnchorArgument.Anchor.EYES, requester.getEyePosition());

        reqData.sendAccept(receiver);
        recData.receiveAccept(requester);

        PlayerAnimationSystem.play(
                receiver,
                interaction.receiver()
        );

        PlayerAnimationSystem.event(
                requester,
                "main"
        );

        PacketDistributor.sendToPlayer(
                requester,
                InteractionControlPacket.accept(receiver, true)
        );

        PacketDistributor.sendToPlayer(
                receiver,
                InteractionControlPacket.accept(requester, false)
        );

        NeoForge.EVENT_BUS.post(new PlayerInteractionEvent.Accept.Post(requester, receiver));

    }

    public static void cancel(
            final @NonNull ServerPlayer requester
    ) {
        final var reqData   = PlayerInteractionSystem.getInteractionData(requester);
        final var sent      = reqData.getSent();

        if (sent == null) {
            return;
        }

        final var level     = requester.level();
        final var other     = level.getPlayerByUUID(sent.getOther());

        if (!(other instanceof ServerPlayer receiver)) {
            return;
        }

        final var recData   = PlayerInteractionSystem.getInteractionData(receiver);

        reqData.cancelSent();
        recData.cancelReceived(requester);

        PacketDistributor.sendToPlayer(
                requester,
                InteractionControlPacket.cancel(receiver, true)
        );

        PacketDistributor.sendToPlayer(
                receiver,
                InteractionControlPacket.cancel(requester, false)
        );

        PlayerAnimationSystem.exit(requester);

        NeoForge.EVENT_BUS.post(new PlayerInteractionEvent.Cancel(requester, receiver));
    }
}
