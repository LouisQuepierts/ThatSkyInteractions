package net.quepierts.thatskyinteractions.feature.friendship;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.friendship.behaviour.FriendshipBehaviourFactory;
import net.quepierts.thatskyinteractions.feature.friendship.packet.PlayerFriendshipControlPacket;
import net.quepierts.thatskyinteractions.feature.gui.packet.PlayerFriendshipUiPacket;
import net.quepierts.thatskyinteractions.feature.interaction.PlayerInteractionSystem;
import net.quepierts.thatskyinteractions.feature.interaction.event.PlayerInteractionEvent;
import net.quepierts.thatskyinteractions.feature.registry.InteractionTypes;
import org.jspecify.annotations.NonNull;

@Slf4j
@UtilityClass
@SuppressWarnings("unused")
@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public class PlayerFriendshipSystem {

    public static final Identifier INTERACTION
            = ThatSkyInteractions.location("unlock");

    public static void invite(
            final @NonNull ServerPlayer requester,
            final @NonNull ServerPlayer receiver,
            final int                   node
    ) {
        final var data      = PlayerFriendshipAttachment.union(requester, receiver);

        if (PlayerFriendshipSystem.isFriendshipConditional() && !data.isUnlockable(node)) {
            return;
        }

        final var treeNode  = data.getStructure().get(node);
        final var cost      = treeNode.getCost();
        final var balance   = CurrencyHelper.getBalance(requester, cost.currency());

        if (balance < cost.amount()) {
            return;
        }

        PlayerFriendshipAttachment.getAttachment(receiver).sendInvite(requester, node);
        if (PlayerInteractionSystem.invite(requester, receiver, INTERACTION)) {

            PacketDistributor.sendToPlayer(
                    receiver,
                    PlayerFriendshipUiPacket.invite(requester)
            );

        }

    }

    // call by Event
    private static void cancel(
            final @NonNull ServerPlayer requester,
            final @NonNull ServerPlayer receiver
    ) {

        PlayerFriendshipAttachment.getAttachment(receiver).removeInvite(requester);

        /*PacketDistributor.sendToPlayer(
                receiver,
                PlayerFriendshipUiPacket.cancel(requester)
        );*/

    }

    public static void accept(
            final @NonNull ServerPlayer requester,
            final @NonNull ServerPlayer receiver
    ) {

        final var attachment    = PlayerFriendshipAttachment.getAttachment(receiver);
        final var id            = attachment.getInvite(requester);
        if (id == -1) {
            return;
        }

        attachment.removeInvite(requester);

        PlayerInteractionSystem.accept(requester, receiver, true);
        PlayerFriendshipSystem.unlock(requester, receiver, id);

    }

    public static boolean unlock(
            final @NonNull ServerPlayer requester,
            final @NonNull ServerPlayer receiver,
            final int node
    ) {

        final var   data = PlayerFriendshipAttachment.union(requester, receiver);

        if (PlayerFriendshipSystem.isFriendshipConditional() && !data.unlock(node)) {
            return false;
        }

        final var treeNode  = data.getStructure().get(node);
        final var cost      = treeNode.getCost();
        final var balance   = CurrencyHelper.getBalance(requester, cost.currency());

        if (balance < cost.amount()) {
            return false;
        }

        CurrencyHelper.consume(requester, cost.currency(), cost.amount());

        PacketDistributor.sendToPlayer(
                requester,
                PlayerFriendshipControlPacket.unlock(
                        receiver.getUUID(),
                        node,
                        true
                )
        );

        PacketDistributor.sendToPlayer(
                receiver,
                PlayerFriendshipControlPacket.unlock(
                        requester.getUUID(),
                        node,
                        false
                )
        );

        return true;
    }

    public static boolean complete(
            final @NonNull ServerPlayer requester,
            final @NonNull ServerPlayer receiver
    ) {

        final var data = PlayerFriendshipAttachment.union(requester, receiver);

        if (data.isCompleted()) {
            return false;
        }

        data.complete();

        PacketDistributor.sendToPlayer(
                requester,
                PlayerFriendshipControlPacket.complete(
                        receiver.getUUID()
                )
        );

        PacketDistributor.sendToPlayer(
                receiver,
                PlayerFriendshipControlPacket.complete(
                        requester.getUUID()
                )
        );

        return true;
    }

    public static boolean reset(
            final @NonNull ServerPlayer requester,
            final @NonNull ServerPlayer receiver
    ) {

        final var data = PlayerFriendshipAttachment.union(requester, receiver);

        if (data.isEmpty()) {
            return false;
        }

        data.reset();

        PacketDistributor.sendToPlayer(
                requester,
                PlayerFriendshipControlPacket.reset(
                        receiver.getUUID()
                )
        );

        PacketDistributor.sendToPlayer(
                receiver,
                PlayerFriendshipControlPacket.reset(
                        requester.getUUID()
                )
        );

        return true;
    }

    public static void drop(final ServerPlayer player) {

        PlayerFriendshipAttachment.getAttachment(player).drop();

        PacketDistributor.sendToPlayer(
                player,
                PlayerFriendshipControlPacket.drop()
        );

    }

    public static boolean interact(
            final @NonNull ServerPlayer requester,
            final @NonNull ServerPlayer receiver,
            final int node
    ) {

        final var data      = PlayerFriendshipAttachment.union(requester, receiver);
        final var level     = requester.level();
        if (PlayerFriendshipSystem.isFriendshipConditional() && !data.isUnlocked(node)) {
            return false;
        }

        final var structure = data.getStructure();
        final var def       = structure.get(node);

        final var behaviour = FriendshipBehaviourFactory.get(def);
        if (behaviour == null) {
            return false;
        }

        behaviour.execute(
                requester,
                receiver,
                def
        );

        return true;
    }

    @SubscribeEvent
    public static void onCancelInteraction(final PlayerInteractionEvent.Cancel event) {

        if (event.isClient()) {
            return;
        }

        if (!event.getInteraction().is(InteractionTypes.UNLOCK)) {
            return;
        }

        PlayerFriendshipSystem.cancel(
                (ServerPlayer) event.getRequester(),
                (ServerPlayer) event.getReceiver()
        );

    }

    /*@SubscribeEvent
    public static void onAcceptedInteraction(final PlayerInteractionEvent.Accept.Post event) {

        if (event.isClient()) {
            return;
        }

        if (!event.getInteraction().equals(PlayerFriendshipSystem.INTERACTION)) {
            return;
        }

        PlayerFriendshipSystem.accept(
                (ServerPlayer) event.getRequester(),
                (ServerPlayer) event.getReceiver()
        );

    }*/

    public static boolean isFriendshipConditional() {
        return ThatSkyInteractions.SERVER_CONFIG.enableConditionalFriendship;
    }
}
