package net.quepierts.thatskyinteractions.feature.gui.packet;

import dev.anvilcraft.lib.v2.network.packet.IClientboundPacket;
import dev.anvilcraft.lib.v2.network.packet.IPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.gui.handler.ClientInteractionUiHandler;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.UUID;

public record PlayerInteractionUiPacket(
        Operation               operation,
        UUID                    requester,
        Identifier              icon
) implements IClientboundPacket {

    public static final Type<PlayerInteractionUiPacket> TYPE
            = IPacket.type(ThatSkyInteractions.location("ui/interaction/player"));

    public static final StreamCodec<ByteBuf, PlayerInteractionUiPacket> STREAM_CODEC
            = StreamCodec.composite(
                    ByteBufCodecs.BYTE.map(
                            Operation::decode,
                            Operation::encode
                    ),
                    PlayerInteractionUiPacket::operation,
                    UUIDUtil.STREAM_CODEC,
                    PlayerInteractionUiPacket::requester,
                    Identifier.STREAM_CODEC,
                    PlayerInteractionUiPacket::icon,
                    PlayerInteractionUiPacket::new
            );

    public static PlayerInteractionUiPacket invite(
            final @NonNull Player requester,
            final @NonNull Identifier icon
    ) {
        return new PlayerInteractionUiPacket(Operation.INVITE, requester.getUUID(), icon);
    }

    @Override
    public void handleOnClient(final @NonNull Player player) {

        final var requester = player.level().getPlayerByUUID(this.requester);
        if (requester == null) {
            return;
        }

        switch (this.operation()) {
            case INVITE: {
                ClientInteractionUiHandler.invite(requester, this.icon());
                break;
            }
        }
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum Operation {
        INVITE;

        static final Operation[] VALUES = values();

        public static Operation decode(byte id) {
            return VALUES[id];
        }

        public byte encode() {
            return (byte) ordinal();
        }
    }
    
}
