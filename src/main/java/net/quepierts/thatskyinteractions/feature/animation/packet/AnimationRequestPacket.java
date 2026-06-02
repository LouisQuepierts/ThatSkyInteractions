package net.quepierts.thatskyinteractions.feature.animation.packet;

import dev.anvilcraft.lib.v2.network.packet.IPacket;
import dev.anvilcraft.lib.v2.network.packet.IServerboundPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public record AnimationRequestPacket(
        Operation               operation,
        Optional<Identifier>    identifier
) implements IServerboundPacket {

    public static final Type<AnimationRequestPacket> TYPE
            = IPacket.type(ThatSkyInteractions.location("animation/request"));

    public static final StreamCodec<ByteBuf, AnimationRequestPacket> STREAM_CODEC
            = StreamCodec.composite(
                    ByteBufCodecs.BYTE.map(
                            Operation::decode,
                            Operation::encode
                    ),
                    AnimationRequestPacket::operation,
                    ByteBufCodecs.optional(Identifier.STREAM_CODEC),
                    AnimationRequestPacket::identifier,
                    AnimationRequestPacket::new
            );

    public static AnimationRequestPacket play(@NonNull Identifier animation) {
        return new AnimationRequestPacket(
                Operation.PLAY,
                Optional.of(animation)
        );
    }

    public static AnimationRequestPacket abort() {
        return new AnimationRequestPacket(
                Operation.ABORT,
                Optional.empty()
        );
    }

    public static AnimationRequestPacket exit() {
        return new AnimationRequestPacket(
                Operation.EXIT,
                Optional.empty()
        );
    }

    public static AnimationRequestPacket event(@NonNull String event) {
        return new AnimationRequestPacket(
                Operation.EVENT,
                Optional.of(Identifier.fromNamespaceAndPath("e", event))
        );
    }

    @Override
    public void handleOnServer(final @NonNull Player player) {

        final var requester = (ServerPlayer) player;

        switch (this.operation()) {
            case PLAY: {
                PlayerAnimationSystem.play(requester, this.identifier().get());
                break;
            }
            case ABORT: {
                PlayerAnimationSystem.abort(requester);
                break;
            }
            case EXIT: {
                PlayerAnimationSystem.exit(requester);
                break;
            }
            case EVENT: {
                PlayerAnimationSystem.event(requester, this.identifier().get().getPath());
                break;
            }
        }
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum Operation {
        PLAY,
        ABORT,
        EXIT,
        EVENT;

        static final Operation[] VALUES = values();

        public static Operation decode(byte id) {
            return VALUES[id];
        }

        public byte encode() {
            return (byte) ordinal();
        }

    }
}
