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

    private static final UUID EMPTY_UUID = new UUID(0, 0);

    public static InteractionRequestPacket invite(
            final @NonNull Player       target,
            final @NonNull Identifier   type
    ) {
        return new InteractionRequestPacket(
                Operation.INVITE,
                target.getUUID(),
                Optional.of(type)
        );
    }

    public static InteractionRequestPacket accept(
            final @NonNull Player       target
    ) {
        return new InteractionRequestPacket(
                Operation.ACCEPT,
                target.getUUID(),
                Optional.empty()
        );
    }

    public static InteractionRequestPacket cancel() {
        return new InteractionRequestPacket(
                Operation.CANCEL,
                EMPTY_UUID,
                Optional.empty()
        );
    }

    @Override
    public void handleOnServer(final @NonNull Player player) {

        final var sender        = (ServerPlayer) player;

        if (this.operation() == Operation.CANCEL) {
            PlayerInteractionSystem.cancel(sender);
            return;
        }

        final var level         = player.level();
        final var target        = level.getPlayerByUUID(this.uuid());

        if (!(target instanceof ServerPlayer other)) {
            return;
        }

        switch (this.operation()) {
            case Operation.INVITE: {
                PlayerInteractionSystem.invite(sender, other, this.identifier().get());
                break;
            }
            case Operation.ACCEPT: {
                PlayerInteractionSystem.accept(other, sender, false);
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
