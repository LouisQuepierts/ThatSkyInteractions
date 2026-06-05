package net.quepierts.thatskyinteractions.feature.friendship.packet;

import dev.anvilcraft.lib.v2.network.packet.IPacket;
import dev.anvilcraft.lib.v2.network.packet.ISensitiveBiPacket;
import dev.anvilcraft.lib.v2.network.packet.IServerboundPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.friendship.PlayerFriendshipSystem;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public record PlayerFriendshipRequestPacket(
        Operation       operation,
        UUID            target,
        int             index
) implements IServerboundPacket {

    public static final Type<PlayerFriendshipRequestPacket> TYPE
            = IPacket.type(ThatSkyInteractions.location("friendship/player/request"));

    public static final StreamCodec<ByteBuf, PlayerFriendshipRequestPacket> STREAM_CODEC
            = StreamCodec.composite(
                    ByteBufCodecs.BYTE.map(
                            Operation::decode,
                            Operation::encode
                    ),
                    PlayerFriendshipRequestPacket::operation,
                    UUIDUtil.STREAM_CODEC,
                    PlayerFriendshipRequestPacket::target,
                    ByteBufCodecs.INT.map(
                            Integer::valueOf,
                            Integer::intValue
                    ),
                    PlayerFriendshipRequestPacket::index,
                    PlayerFriendshipRequestPacket::new
            );

    public static PlayerFriendshipRequestPacket unlock(
            final @NonNull UUID target,
            final int index
    ) {
        return new PlayerFriendshipRequestPacket(
                Operation.UNLOCK,
                target,
                index
        );
    }

    public static PlayerFriendshipRequestPacket interact(
            final @NonNull UUID target,
            final int index
    ) {
        return new PlayerFriendshipRequestPacket(
                Operation.INTERACT,
                target,
                index
        );
    }

    @Override
    public void handleOnServer(final @NonNull Player player) {

        final var requester = (ServerPlayer) player;
        final var level     = requester.level();
        final var other     = level.getPlayerByUUID(this.target);

        if (!(other instanceof ServerPlayer receiver)) {
            return;
        }

        switch (this.operation) {
            case UNLOCK: {
                PlayerFriendshipSystem.unlock(requester, receiver, this.index);
                break;
            }
            case INTERACT: {
                PlayerFriendshipSystem.interact(requester, receiver, this.index);
                break;
            }
        }
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum Operation {
        UNLOCK,
        INTERACT;

        static final Operation[] VALUES = values();

        public static Operation decode(byte id) {
            return VALUES[id];
        }

        public byte encode() {
            return (byte) ordinal();
        }
    }

}
