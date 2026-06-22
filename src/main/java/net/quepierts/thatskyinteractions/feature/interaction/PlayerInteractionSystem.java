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
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.bond.PlayerBondSystem;
import net.quepierts.thatskyinteractions.feature.control.packet.NavigatePacket;
import net.quepierts.thatskyinteractions.feature.expression.PlayerExpressionSystem;
import net.quepierts.thatskyinteractions.feature.interaction.event.PlayerInteractionEvent;
import net.quepierts.thatskyinteractions.feature.interaction.packet.ClientboundInteractionControlPacket;
import net.quepierts.thatskyinteractions.feature.utils.PlayerUtils;
import org.jspecify.annotations.NonNull;

@Slf4j
@UtilityClass
public class PlayerInteractionSystem {

    public static final String ANIMATION_TYPE_REQUESTER = "interaction.requester";
    public static final String ANIMATION_TYPE_RECEIVER  = "interaction.receiver";

    public static final Identifier EXPRESSION_REQUESTER
            = ThatSkyInteractions.location("interaction.requester");

    public static final Identifier EXPRESSION_RECEIVER
            = ThatSkyInteractions.location("interaction.receiver");

    public static PlayerInteractionAttachment getInteractionAttachment(
            final @NonNull Player player
    ) {
        return PlayerInteractionAttachment.getAttachment(player);
    }

    /*
    * Order matters
    * Requester
    * */
    public static boolean invite(
            final @NonNull ServerPlayer requester,
            final @NonNull ServerPlayer receiver,
            final @NonNull Identifier   interactionId
    ) {

        if (requester.is(receiver)) {
            return false;
        }

        if (PlayerBondSystem.isBondedWith(requester, receiver)) { // temporary solution
            return false;
        }

        final var manager       = PlayerInteractionManager.getInstance();
        final var interaction    = manager.get(interactionId);

        if (interaction == null) {
            return false;
        }

        final ICancellableEvent event = NeoForge.EVENT_BUS.post(new PlayerInteractionEvent.Invite.Pre(requester, receiver, interactionId));
        if (event.isCanceled()) {
            return false;
        }

        final var level = requester.level();
        if (level != receiver.level()) {
            return false;
        }

        if (requester.distanceToSqr(receiver) > 256) {
            return false;
        }

        final var reqData = PlayerInteractionSystem.getInteractionAttachment(requester);
        final var recData = PlayerInteractionSystem.getInteractionAttachment(receiver);

        if (reqData.hasSentRequest()) {
            PlayerInteractionSystem.cancel(requester);
        }

        // delegate
        if (!PlayerExpressionSystem.perform(requester, EXPRESSION_REQUESTER)) {
            return false;
        }

        interaction.onInvite(requester, receiver);

        reqData.sendInvite(receiver, interactionId);
        recData.receiveInvite(requester, interactionId);

        PacketDistributor.sendToPlayer(
                requester,
                ClientboundInteractionControlPacket.invite(receiver, interactionId, true)
        );

        PacketDistributor.sendToPlayer(
                receiver,
                ClientboundInteractionControlPacket.invite(requester, interactionId, false)
        );

        NeoForge.EVENT_BUS.post(new PlayerInteractionEvent.Invite.Post(requester, receiver, interactionId));

        return true;
    }

    public static boolean accept(
            final @NonNull ServerPlayer requester,
            final @NonNull ServerPlayer receiver,
            final boolean               force
    ) {

        if (requester.is(receiver)) {
            return false;
        }

        final var reqData = PlayerInteractionSystem.getInteractionAttachment(requester);
        final var recData = PlayerInteractionSystem.getInteractionAttachment(receiver);

        final var sent = reqData.getOngoing();
        if (sent == null || !sent.getOther().equals(receiver.getUUID())) {
            return false;
        }

        if (!recData.hasReceivedRequest(requester.getUUID())) {
            return false;
        }

        final var level = requester.level();
        if (level != receiver.level()) {
            return false;
        }

        final var type      = sent.getType();
        final var manager   = PlayerInteractionManager.getInstance();

        final var interaction = manager.get(type);

        if (interaction == null) {
            return false;
        }

        final var event     = NeoForge.EVENT_BUS.post(new PlayerInteractionEvent.Accept.Pre(requester, receiver, type));
        if (event.isCanceled()) {
            return false;
        }

        if (PlayerExpressionSystem.isPerforming(receiver)) {
            PlayerExpressionSystem.interrupt(receiver);
            return false;
        }

        if (interaction.positional()) {
            final var position = PlayerUtils.getRelativePositionWorldSpace(requester, 1.0, 0.0);

            if (!force && receiver.distanceToSqr(position) > 1e-3) {

                final var lookTarget = EntityAnchorArgument.Anchor.EYES.apply(requester);

                PacketDistributor.sendToPlayer(
                        receiver,
                        new NavigatePacket(position, lookTarget)
                );
                return false;
            }

            receiver.teleportTo(position.x, position.y, position.z);
            receiver.lookAt(EntityAnchorArgument.Anchor.EYES, requester.getEyePosition());
        }

        if (!PlayerExpressionSystem.perform(receiver, EXPRESSION_RECEIVER)) {
            return false;
        }

        interaction.onAccepted(requester, receiver);

        reqData.sendAccept(receiver);
        recData.receiveAccept(requester);

        PacketDistributor.sendToPlayer(
                requester,
                ClientboundInteractionControlPacket.accept(receiver, true)
        );

        PacketDistributor.sendToPlayer(
                receiver,
                ClientboundInteractionControlPacket.accept(requester, false)
        );

        NeoForge.EVENT_BUS.post(new PlayerInteractionEvent.Accept.Post(requester, receiver, type));

        return false;

    }

    public static void cancel(
            final @NonNull ServerPlayer requester
    ) {
        final var reqData   = PlayerInteractionSystem.getInteractionAttachment(requester);
        final var sent      = reqData.getOngoing();


        if (sent == null) {
            return;
        }

        final var level     = requester.level();
        final var otherUUID = sent.getOther();
        final var other     = level.getPlayerByUUID(otherUUID);

        final var type      = reqData.getOngoing().getType();

        // change order
        // if other is not online, requester still can cancel the invite
        reqData.cancelSent();

        PacketDistributor.sendToPlayer(
                requester,
                ClientboundInteractionControlPacket.cancel(otherUUID, true)
        );

        final var manager   = PlayerInteractionManager.getInstance();
        final var interaction = manager.get(type);

        if (interaction != null) {
            interaction.onCancel(requester, (ServerPlayer) other);
        }

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
