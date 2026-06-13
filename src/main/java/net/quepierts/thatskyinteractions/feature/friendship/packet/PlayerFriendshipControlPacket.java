package net.quepierts.thatskyinteractions.feature.friendship.packet;

import dev.anvilcraft.lib.v2.network.packet.IClientboundPacket;
import dev.anvilcraft.lib.v2.network.packet.IPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.friendship.model.Cost;
import net.quepierts.thatskyinteractions.core.friendship.model.FriendshipTreeNode;
import net.quepierts.thatskyinteractions.feature.friendship.CurrencyHelper;
import net.quepierts.thatskyinteractions.feature.friendship.PlayerFriendshipAttachment;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public record PlayerFriendshipControlPacket(
        Operation       operation,
        UUID            target,
        int             index
) implements IClientboundPacket {

    public static final Type<PlayerFriendshipControlPacket> TYPE
            = IPacket.type(ThatSkyInteractions.location("friendship/player/control"));

    public static final StreamCodec<ByteBuf, PlayerFriendshipControlPacket> STREAM_CODEC
            = StreamCodec.composite(
                    ByteBufCodecs.BYTE.map(
                            Operation::decode,

                            Operation::encode
                    ),
                    PlayerFriendshipControlPacket::operation,
                    UUIDUtil.STREAM_CODEC,
                    PlayerFriendshipControlPacket::target,
                    ByteBufCodecs.INT.map(
                            Integer::valueOf,
                            Integer::intValue
                    ),
                    PlayerFriendshipControlPacket::index,
                    PlayerFriendshipControlPacket::new
            );

    private static final UUID DUMMY = new UUID(0, 0);

    public static PlayerFriendshipControlPacket unlock(
            final @NonNull UUID target,
            final int index,
            final boolean requester
    ) {
        return new PlayerFriendshipControlPacket(requester ? Operation.UNLOCK_REQ : Operation.UNLOCK_REC, target, index);
    }

    public static PlayerFriendshipControlPacket complete(
            final @NonNull UUID target
    ) {
        return new PlayerFriendshipControlPacket(Operation.COMPLETE, target, -1);
    }

    public static PlayerFriendshipControlPacket reset(
            final @NonNull UUID target
    ) {
        return new PlayerFriendshipControlPacket(Operation.RESET, target, -1);
    }

    public static PlayerFriendshipControlPacket drop() {
        return new PlayerFriendshipControlPacket(Operation.DROP, DUMMY, -1);
    }

    @Override
    public void handleOnClient(final @NonNull Player player) {

        final var attachment    = PlayerFriendshipAttachment.getAttachment(player);
        final var data          = attachment.get(this.target(), PlayerFriendshipAttachment.FRIEND);

        switch (this.operation()) {
            case UNLOCK_REQ: {
                data.unlock(this.index());
                final var node = data.getStructure().get(this.index());
                final var cost = node.getCost();
                CurrencyHelper.consume(player, cost.currency(), cost.amount());
                break;
            }
            case UNLOCK_REC: {
                data.unlock(this.index());
                break;
            }
            case COMPLETE: {
                data.complete();
                break;
            }
            case RESET: {
                data.reset();
                break;
            }
            case DROP: {
                attachment.drop();
                break;
            }
        }
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum Operation {
        UNLOCK_REQ,
        UNLOCK_REC,
        COMPLETE,
        RESET,
        DROP;

        static final Operation[] VALUES = values();

        public static Operation decode(byte id) {
            return VALUES[id];
        }

        public byte encode() {
            return (byte) ordinal();
        }
    }

}
