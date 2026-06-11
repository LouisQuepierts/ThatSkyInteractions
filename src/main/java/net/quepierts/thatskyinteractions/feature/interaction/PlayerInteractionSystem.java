package net.quepierts.thatskyinteractions.feature.interaction;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import net.quepierts.thatskyinteractions.core.interaction.DefaultInteractionFSM;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.control.packet.NavigatePacket;
import net.quepierts.thatskyinteractions.feature.interaction.event.PlayerInteractionEvent;
import net.quepierts.thatskyinteractions.feature.interaction.packet.ClientboundInteractionControlPacket;
import net.quepierts.thatskyinteractions.feature.utils.PlayerUtils;
import org.jspecify.annotations.NonNull;

@Slf4j
@UtilityClass
public class PlayerInteractionSystem {

    public static final String ANIMATION_TYPE_REQUESTER = "interaction.requester";
    public static final String ANIMATION_TYPE_RECEIVER  = "interaction.receiver";

    public static PlayerInteractionAttachment getInteractionAttachment(
            @NonNull final Player player
    ) {
        return PlayerInteractionAttachment.getAttachment(player);
    }

    /*
    * Order matters
    * Requester
    * */
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

        final var reqData = PlayerInteractionSystem.getInteractionAttachment(requester);
        final var recData = PlayerInteractionSystem.getInteractionAttachment(receiver);

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
                ClientboundInteractionControlPacket.invite(receiver, interaction, true)
        );

        PacketDistributor.sendToPlayer(
                receiver,
                ClientboundInteractionControlPacket.invite(requester, interaction, false)
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

        final var reqData = PlayerInteractionSystem.getInteractionAttachment(requester);
        final var recData = PlayerInteractionSystem.getInteractionAttachment(receiver);

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

        final var event     = NeoForge.EVENT_BUS.post(new PlayerInteractionEvent.Accept.Pre(requester, receiver, type));
        if (event.isCanceled()) {
            return;
        }

        final var position = PlayerUtils.getRelativePositionWorldSpace(requester, 1.0, 0.0);

        if (interaction.positional() && !force && receiver.distanceToSqr(position) > 1e-3) {

            final var lookTarget = EntityAnchorArgument.Anchor.EYES.apply(requester);

            PacketDistributor.sendToPlayer(
                    receiver,
                    new NavigatePacket(position, lookTarget)
            );
            return;
        }

        if (interaction.positional()) {
            receiver.teleportTo(position.x, position.y, position.z);
            receiver.lookAt(EntityAnchorArgument.Anchor.EYES, requester.getEyePosition());
        }

        reqData.sendAccept(receiver);
        recData.receiveAccept(requester);

        PlayerAnimationSystem.play(
                receiver,
                interaction.receiver()
        );

        PlayerAnimationSystem.signal(
                requester,
                DefaultInteractionFSM.REQUESTER_ACCEPT
        );

        PacketDistributor.sendToPlayer(
                requester,
                ClientboundInteractionControlPacket.accept(receiver, true)
        );

        PacketDistributor.sendToPlayer(
                receiver,
                ClientboundInteractionControlPacket.accept(requester, false)
        );

        NeoForge.EVENT_BUS.post(new PlayerInteractionEvent.Accept.Post(requester, receiver, type));

    }

    public static void cancel(
            final @NonNull ServerPlayer requester
    ) {
        final var reqData   = PlayerInteractionSystem.getInteractionAttachment(requester);
        final var sent      = reqData.getSent();


        if (sent == null) {
            return;
        }

        final var level     = requester.level();
        final var otherUUID = sent.getOther();
        final var other     = level.getPlayerByUUID(otherUUID);

        final var type      = reqData.getSent().getType();

        // change order
        // if other is not online, requester still can cancel the invite
        reqData.cancelSent();

        PacketDistributor.sendToPlayer(
                requester,
                ClientboundInteractionControlPacket.cancel(otherUUID, true)
        );

        PlayerAnimationSystem.exit(requester);

        if (other instanceof ServerPlayer receiver) {

            final var recData   = PlayerInteractionSystem.getInteractionAttachment(receiver);
            recData             .cancelReceived(requester);

            PacketDistributor.sendToPlayer(
                    receiver,
                    ClientboundInteractionControlPacket.cancel(requester.getUUID(), false)
            );

            NeoForge.EVENT_BUS.post(new PlayerInteractionEvent.Cancel(requester, other, type));
        }

    }

}
