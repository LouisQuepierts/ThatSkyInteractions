package net.quepierts.thatskyinteractions.feature.animation.packet;

import dev.anvilcraft.lib.v2.network.packet.IClientboundPacket;
import dev.anvilcraft.lib.v2.network.packet.IPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.registry.TsiRegistries;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public record ClientboundAnimationControlPacket(
        Operation               operation,
        int                     id,
        Optional<Identifier>    layer,
        Optional<Identifier>    identifier
) implements IClientboundPacket {

    public static final Type<ClientboundAnimationControlPacket> TYPE
            = IPacket.type(ThatSkyInteractions.location("animation/control"));

    public static final StreamCodec<ByteBuf, ClientboundAnimationControlPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE.map(
                    Operation::decode,
                    Operation::encode
            ),
            ClientboundAnimationControlPacket::operation,
            ByteBufCodecs.VAR_INT,
            ClientboundAnimationControlPacket::id,
            ByteBufCodecs.optional(Identifier.STREAM_CODEC),
            ClientboundAnimationControlPacket::layer,
            ByteBufCodecs.optional(Identifier.STREAM_CODEC),
            ClientboundAnimationControlPacket::identifier,
            ClientboundAnimationControlPacket::new
    );

    public static ClientboundAnimationControlPacket play(
            @NonNull LivingEntity   player,
            @NonNull Identifier     animation
    ) {
        return new ClientboundAnimationControlPacket(
                Operation.PLAY,
                player.getId(),
                Optional.empty(),
                Optional.of(animation)
        );
    }

    public static ClientboundAnimationControlPacket play(
            @NonNull LivingEntity   player,
            @NonNull Identifier     animation,
            @NonNull Identifier     layer
    ) {
        return new ClientboundAnimationControlPacket(
                Operation.PLAY,
                player.getId(),
                Optional.of(layer),
                Optional.of(animation)
        );
    }

    public static ClientboundAnimationControlPacket abort(
            @NonNull LivingEntity   player
    ) {
        return new ClientboundAnimationControlPacket(
                Operation.ABORT,
                player.getId(),
                Optional.empty(),
                Optional.empty()
        );
    }

    public static ClientboundAnimationControlPacket abort(
            @NonNull LivingEntity   player,
            @NonNull Identifier     layer
    ) {
        return new ClientboundAnimationControlPacket(
                Operation.ABORT,
                player.getId(),
                Optional.of(layer),
                Optional.empty()
        );
    }

    public static ClientboundAnimationControlPacket exit(
            @NonNull LivingEntity   player
    ) {
        return new ClientboundAnimationControlPacket(
                Operation.EXIT,
                player.getId(),
                Optional.empty(),
                Optional.empty()
        );
    }

    public static ClientboundAnimationControlPacket exit(
            @NonNull LivingEntity   player,
            @NonNull Identifier     layer
    ) {
        return new ClientboundAnimationControlPacket(
                Operation.EXIT,
                player.getId(),
                Optional.of(layer),
                Optional.empty()
        );
    }

    public static ClientboundAnimationControlPacket pause(
            @NonNull LivingEntity   player
    ) {
        return new ClientboundAnimationControlPacket(
                Operation.PAUSE,
                player.getId(),
                Optional.empty(),
                Optional.empty()
        );
    }

    public static ClientboundAnimationControlPacket pause(
            @NonNull LivingEntity   player,
            @NonNull Identifier     layer
    ) {
        return new ClientboundAnimationControlPacket(
                Operation.PAUSE,
                player.getId(),
                Optional.of(layer),
                Optional.empty()
        );
    }

    public static ClientboundAnimationControlPacket resume(
            @NonNull LivingEntity   player
    ) {
        return new ClientboundAnimationControlPacket(
                Operation.RESUME,
                player.getId(),
                Optional.empty(),
                Optional.empty()
        );
    }

    public static ClientboundAnimationControlPacket resume(
            @NonNull LivingEntity   player,
            @NonNull Identifier layer
    ) {
        return new ClientboundAnimationControlPacket(
                Operation.RESUME,
                player.getId(),
                Optional.of(layer),
                Optional.empty()
        );
    }

    public static ClientboundAnimationControlPacket event(
            @NonNull LivingEntity   player,
            @NonNull String         event
    ) {
        return new ClientboundAnimationControlPacket(
                Operation.EVENT,
                player.getId(),
                Optional.empty(),
                Optional.of(Identifier.fromNamespaceAndPath("e", event))
        );
    }

    public static ClientboundAnimationControlPacket event(
            @NonNull LivingEntity   player,
            @NonNull String         event,
            @NonNull Identifier     layer
    ) {
        return new ClientboundAnimationControlPacket(
                Operation.EVENT,
                player.getId(),
                Optional.of(layer),
                Optional.of(Identifier.fromNamespaceAndPath("e", event))
        );
    }

    @Override
    public void handleOnClient(final @NonNull Player player) {
        final var level     = player.level();
        final var target    = level.getEntity(this.id());

        LivingEntity avatar;
        if ((avatar = PlayerAnimationSystem.tryParseAnimatable(target)) == null) {
            return;
        }

        final var layer     = TsiRegistries.ANIMATION_LAYER_TYPE
                            .getOptional(this.layer().orElse(null))
                            .orElse(null);

        final var attachment    = PlayerAnimationSystem.getAnimationData(avatar);
        final var controller    = attachment.getController();

        switch (this.operation) {
            case PLAY: {
                final var animationId = this.identifier.orElseThrow();
                if (layer == null) {
                    controller.play(animationId);
                } else {
                    controller.play(animationId, layer);
                }
                attachment.setupScene(avatar);
                break;
            }
            case PAUSE: {
                controller.pause(layer);
                break;
            }
            case RESUME: {
                controller.resume(layer);
                break;
            }
            case ABORT: {
                controller.abort(layer);
                break;
            }
            case EXIT: {
                controller.exit(layer);
                break;
            }
            case EVENT: {
                if (controller.isPlaying()) {
                    final var path      = this.identifier.orElseThrow().getPath();
                    controller.event(path);
                    /*final var current   = controller.getCurrent();
                    final var animation = current.getAnimation();
                    final var fsm       = animation.getFsm();
                    final var event     = path.equals("exit") ? -1 : fsm.getLookup().find(path);

                    animation.event(current.getFsmState(), event);*/
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
