package net.quepierts.thatskyinteractions.feature.friendship;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.quepierts.thatskyinteractions.feature.friendship.packet.PlayerFriendshipControlPacket;
import org.jspecify.annotations.NonNull;

@Slf4j
@UtilityClass
public class PlayerFriendshipSystem {

    public static boolean unlock(
            final @NonNull ServerPlayer requester,
            final @NonNull ServerPlayer receiver,
            final int node
    ) {
        if (requester.level().isClientSide()) {
            log.debug("Cannot unlock friendship tree on client side!");
            return false;
        }

        final var   data = PlayerFriendshipAttachment.union(requester, receiver);

        if (!data.unlock(node)) {
            return false;
        }

        PacketDistributor.sendToPlayer(
                requester,
                PlayerFriendshipControlPacket.unlock(
                        receiver.getUUID(),
                        node
                )
        );

        PacketDistributor.sendToPlayer(
                receiver,
                PlayerFriendshipControlPacket.unlock(
                        requester.getUUID(),
                        node
                )
        );

        final var name = data.getStructure().getLookup().name(node);
        log.debug("{} unlocked friendship tree node {}", requester.getName().getString(), name);

        return true;
    }

    public static boolean compile(
            final @NonNull ServerPlayer requester,
            final @NonNull ServerPlayer receiver
    ) {
        if (requester.level().isClientSide()) {
            log.debug("Cannot compile friendship tree on client side!");
            return false;
        }

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
        if (requester.level().isClientSide()) {
            log.debug("Cannot reset friendship tree on client side!");
            return false;
        }

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

    public static void interact(
            final @NonNull ServerPlayer requester,
            final @NonNull ServerPlayer receiver,
            final int node
    ) {

    }

    public static PlayerFriendshipAttachment getFriendshipAttachment(
            final @NonNull ServerPlayer player
    ) {
        return PlayerFriendshipAttachment.getAttachment(player);
    }

}
