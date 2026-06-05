package net.quepierts.thatskyinteractions.feature.animation.packet;

import dev.anvilcraft.lib.v2.network.packet.IClientboundPacket;
import dev.anvilcraft.lib.v2.network.packet.IPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.animation.tween.PhysicalTweenAttachment;
import net.quepierts.thatskyinteractions.feature.utils.Interpolators;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Eases;
import net.quepierts.veynir.core.adapter.Consumer1f;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.UUID;

public record AnimationControlPacket(
        Operation               operation,
        UUID                    uuid,
        Optional<Identifier>    identifier
) implements IClientboundPacket {

    public static final Type<AnimationControlPacket> TYPE
            = IPacket.type(ThatSkyInteractions.location("animation/control"));

    public static final StreamCodec<ByteBuf, AnimationControlPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE.map(
                    Operation::decode,
                    Operation::encode
            ),
            AnimationControlPacket::operation,
            UUIDUtil.STREAM_CODEC,
            AnimationControlPacket::uuid,
            ByteBufCodecs.optional(Identifier.STREAM_CODEC),
            AnimationControlPacket::identifier,
            AnimationControlPacket::new
    );

    public static AnimationControlPacket play(
            @NonNull Player     player,
            @NonNull Identifier animation
    ) {
        return new AnimationControlPacket(
                Operation.PLAY,
                player.getUUID(),
                Optional.of(animation)
        );
    }

    public static AnimationControlPacket abort(
            @NonNull Player     player
    ) {
        return new AnimationControlPacket(
                Operation.ABORT,
                player.getUUID(),
                Optional.empty()
        );
    }

    public static AnimationControlPacket exit(
            @NonNull Player     player
    ) {
        return new AnimationControlPacket(
                Operation.EXIT,
                player.getUUID(),
                Optional.empty()
        );
    }

    public static AnimationControlPacket pause(
            @NonNull Player     player
    ) {
        return new AnimationControlPacket(
                Operation.PAUSE,
                player.getUUID(),
                Optional.empty()
        );
    }

    public static AnimationControlPacket resume(
            @NonNull Player     player
    ) {
        return new AnimationControlPacket(
                Operation.RESUME,
                player.getUUID(),
                Optional.empty()
        );
    }

    public static AnimationControlPacket event(
            @NonNull Player     player,
            @NonNull Identifier event
    ) {
        return new AnimationControlPacket(
                Operation.EVENT,
                player.getUUID(),
                Optional.of(event)
        );
    }

    @Override
    public void handleOnClient(final @NonNull Player player) {
        final var level     = player.level();
        final var target    = level.getPlayerByUUID(this.uuid());

        if (target == null) {
            return;
        }

        final var data          = PlayerAnimationSystem.getAnimationData(target);
        final var controller    = data.getController();

        switch (this.operation) {
            case PLAY: {

                final var difference = Mth.degreesDifference(target.yBodyRot, target.getYHeadRot());

                PhysicalTweenAttachment.getAttachment(player.level()).tween().to(
                        target::setYBodyRot,
                        target.yBodyRot,
                        target.getYHeadRot(),
                        Mth.abs(difference) * 0.01f,
                        Interpolators.DEGREE,
                        Eases.CUBIC_OUT
                );
                controller.play(this.identifier.get());
                break;
            }
            case PAUSE: {
                controller.pause();
                break;
            }
            case RESUME: {
                controller.resume();
                break;
            }
            case ABORT: {
                controller.abort();
                break;
            }
            case EXIT: {
                controller.exit();
                break;
            }
            case EVENT: {
                if (controller.isPlaying()) {
                    final var animation = controller.getAnimation();
                    final var fsm       = animation.getFsm();
                    final var path      = this.identifier.get().getPath();
                    final var event     = path.equals("exit") ? -1 : fsm.getLookup().find(path);

                    animation.event(controller.getFsmState(), event);
                }
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
        PAUSE,
        RESUME,
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
