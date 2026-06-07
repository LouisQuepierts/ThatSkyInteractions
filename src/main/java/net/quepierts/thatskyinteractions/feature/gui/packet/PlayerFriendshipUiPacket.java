package net.quepierts.thatskyinteractions.feature.gui.packet;

import dev.anvilcraft.lib.v2.network.packet.IClientboundPacket;
import dev.anvilcraft.lib.v2.network.packet.IPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.gui.handler.ClientFriendshipUiHandler;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public record PlayerFriendshipUiPacket(
        Operation       operation,
        UUID            target
) implements IClientboundPacket {

    public static final Type<PlayerFriendshipUiPacket> TYPE
            = IPacket.type(ThatSkyInteractions.location("ui/friendship/player"));

    public static final StreamCodec<ByteBuf, PlayerFriendshipUiPacket> STREAM_CODEC
            = StreamCodec.composite(
                    ByteBufCodecs.BYTE.map(
                            Operation::decode,
                            Operation::encode
                    ),
                    PlayerFriendshipUiPacket::operation,
                    UUIDUtil.STREAM_CODEC,
                    PlayerFriendshipUiPacket::target,
                    PlayerFriendshipUiPacket::new
            );

    public static PlayerFriendshipUiPacket invite(
            @NonNull final Player       requester
    ) {
        return new PlayerFriendshipUiPacket(
                Operation.INVITE,
                requester.getUUID()
        );
    }

    public static PlayerFriendshipUiPacket cancel(
            @NonNull final Player       requester
    ) {
        return new PlayerFriendshipUiPacket(
                Operation.CANCEL,
                requester.getUUID()
        );
    }

    @Override
    public void handleOnClient(final @NonNull Player player) {

        final var requester     = player.level().getPlayerByUUID(this.target());
        if (requester == null) {
            return;
        }

        switch (this.operation()) {
            case INVITE: {
                ClientFriendshipUiHandler.invite(requester);
                break;
            }

            case CANCEL: {
                ClientFriendshipUiHandler.cancel(requester);
                break;
            }
        }
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum Operation {
        INVITE,
        CANCEL;

        static final Operation[] VALUES = values();

        public static Operation decode(byte id) {
            return VALUES[id];
        }

        public byte encode() {
            return (byte) ordinal();
        }
    }

}
