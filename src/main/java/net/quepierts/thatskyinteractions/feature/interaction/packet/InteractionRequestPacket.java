package net.quepierts.thatskyinteractions.feature.interaction.packet;

import dev.anvilcraft.lib.v2.network.packet.IPacket;
import dev.anvilcraft.lib.v2.network.packet.IServerboundPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.interaction.PlayerInteractionSystem;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.UUID;

public record InteractionRequestPacket(
        Operation               operation,
        UUID                    uuid,
        Optional<Identifier>    identifier
) implements IServerboundPacket {

    public static final Type<InteractionRequestPacket> TYPE
            = IPacket.type(ThatSkyInteractions.location("interaction/request"));

    public static final StreamCodec<ByteBuf, InteractionRequestPacket> STREAM_CODEC
            = StreamCodec.composite(
                    ByteBufCodecs.BYTE.map(
                            Operation::decode,
                            Operation::encode
                    ),
                    InteractionRequestPacket::operation,
                    UUIDUtil.STREAM_CODEC,
                    InteractionRequestPacket::uuid,
                    ByteBufCodecs.optional(Identifier.STREAM_CODEC),
                    InteractionRequestPacket::identifier,
                    InteractionRequestPacket::new
            );

    @Override
    public void handleOnServer(final @NonNull Player player) {

        final var sender        = (ServerPlayer) player;

        if (this.operation() == Operation.CANCEL) {
            PlayerInteractionSystem.cancel(sender);
            return;
        }

        final var level         = player.level();
        final var target        = level.getPlayerByUUID(this.uuid());

        if (!(target instanceof ServerPlayer receiver)) {
            return;
        }

        switch (this.operation()) {
            case Operation.INVITE: {
                PlayerInteractionSystem.invite(sender, receiver, this.identifier().get());
                break;
            }
            case Operation.ACCEPT: {
                PlayerInteractionSystem.accept(sender, receiver, false);
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
        ACCEPT,
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
