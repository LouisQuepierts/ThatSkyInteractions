package net.quepierts.thatskyinteractions.feature.interaction.packet;

import dev.anvilcraft.lib.v2.network.packet.IClientboundPacket;
import dev.anvilcraft.lib.v2.network.packet.IPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.interaction.PlayerInteractionSystem;
import net.quepierts.thatskyinteractions.feature.interaction.event.PlayerInteractionEvent;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.UUID;

public record ClientboundInteractionControlPacket(
        Operation               operation,
        UUID                    uuid,
        Optional<Identifier>    identifier
) implements IClientboundPacket {

    public static final Type<ClientboundInteractionControlPacket> TYPE
            = IPacket.type(ThatSkyInteractions.location("interaction/control"));

    public static final StreamCodec<ByteBuf, ClientboundInteractionControlPacket> STREAM_CODEC
            = StreamCodec.composite(
                    ByteBufCodecs.BYTE.map(
                            Operation::decode,
                            Operation::encode
                    ),
                    ClientboundInteractionControlPacket::operation,
                    UUIDUtil.STREAM_CODEC,
                    ClientboundInteractionControlPacket::uuid,
                    ByteBufCodecs.optional(Identifier.STREAM_CODEC),
                    ClientboundInteractionControlPacket::identifier,
                    ClientboundInteractionControlPacket::new
            );


    public static ClientboundInteractionControlPacket invite(
            final @NonNull Player       requester,
            final @NonNull Identifier   type,
            final boolean               send
    ) {
        return new ClientboundInteractionControlPacket(
                send ? Operation.INVITE_REQ : Operation.INVITE_REC,
                requester.getUUID(),
                Optional.of(type)
        );
    }

    public static ClientboundInteractionControlPacket accept(
            final @NonNull Player       requester,
            final boolean               send
    ) {
        return new ClientboundInteractionControlPacket(
                send ? Operation.ACCEPT_REQ :Operation.ACCEPT_REC,
                requester.getUUID(),
                Optional.empty()
        );
    }

    public static ClientboundInteractionControlPacket cancel(
            final @NonNull UUID         uuid,
            final boolean               send
    ) {
        return new ClientboundInteractionControlPacket(
                send ? Operation.CANCEL_REQ :Operation.CANCEL_REC,
                uuid,
                Optional.empty()
        );
    }

    @Override
    public void handleOnClient(final @NonNull Player player) {

        final var level         = player.level();
        final var target        = level.getPlayerByUUID(this.uuid());

        if (this.operation() != Operation.CANCEL_REQ && target == null) {
            return;
        }

        final var data          = PlayerInteractionSystem.getInteractionAttachment(player);

        switch (this.operation()) {
            case INVITE_REQ: {
                final var interaction = this.identifier().orElseThrow(); // it supposes to be present
                data.sendInvite(target, interaction);
                NeoForge.EVENT_BUS.post(new PlayerInteractionEvent.Invite.Post(player, target, interaction));
                break;
            }
            case ACCEPT_REQ: {
                final var send = data.getSent();
                if (send != null) {
                    final var interaction = send.getType();
                    data.sendAccept(target);
                    NeoForge.EVENT_BUS.post(new PlayerInteractionEvent.Accept.Post(player, target, interaction));
                }
                break;
            }
            case CANCEL_REQ: {
                final var sent = data.getSent();
                if (sent != null) {
                    final var interaction = sent.getType();
                    data.cancelSent();

                    if (target != null) {
                        NeoForge.EVENT_BUS.post(new PlayerInteractionEvent.Cancel(player, target, interaction));
                    }
                }
                break;
            }
            case INVITE_REC: {
                final var interaction = this.identifier().orElseThrow(); // it supposes to be present
                data.receiveInvite(target, interaction);
                NeoForge.EVENT_BUS.post(new PlayerInteractionEvent.Invite.Post(target, player, interaction));
                break;
            }
            case ACCEPT_REC: {
                final var request = data.getReceived().get(this.uuid());
                if (request != null) {
                    final var interaction = request.getType();
                    data.receiveAccept(target);
                    NeoForge.EVENT_BUS.post(new PlayerInteractionEvent.Accept.Post(target, player, interaction));
                }
                break;
            }
            case CANCEL_REC: {
                final var request = data.getReceived().get(this.uuid());
                if (request != null) {
                    final var interaction = request.getType();
                    data.cancelReceived(target);
                    NeoForge.EVENT_BUS.post(new PlayerInteractionEvent.Cancel(target, player, interaction));
                }
                break;
            }
        }

    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum Operation {
        INVITE_REQ,
        ACCEPT_REQ,
        CANCEL_REQ,
        INVITE_REC,
        ACCEPT_REC,
        CANCEL_REC;

        static final Operation[] VALUES = values();

        public static Operation decode(byte id) {
            return VALUES[id];
        }

        public byte encode() {
            return (byte) ordinal();
        }
    }

}
