package net.quepierts.thatskyinteractions.feature.friendship;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.quepierts.thatskyinteractions.feature.friendship.behaviour.FriendshipBehaviourFactory;
import net.quepierts.thatskyinteractions.feature.friendship.packet.PlayerFriendshipControlPacket;
import org.jspecify.annotations.NonNull;

@Slf4j
@UtilityClass
@SuppressWarnings("unused")
public class PlayerFriendshipSystem {

    public static boolean unlock(
            final @NonNull ServerPlayer requester,
            final @NonNull ServerPlayer receiver,
            final int node
    ) {

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
        if (!data.isUnlocked(node)) {
            return false;
        }

        final var structure = data.getStructure();
        final var def       = structure.get(node);

        final var behaviour = FriendshipBehaviourFactory.get(def);
        if (behaviour == null) {
            return false;
        }

        behaviour.execute(
                PlayerFriendshipAttachment.getAttachment(requester),
                def
        );

        return true;
    }
}
