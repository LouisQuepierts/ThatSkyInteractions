package net.quepierts.thatskyinteractions.feature.animation.packet;

import dev.anvilcraft.lib.v2.network.packet.IClientboundPacket;
import dev.anvilcraft.lib.v2.network.packet.IPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationController;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import org.jspecify.annotations.NonNull;

public record ClientboundSyncAnimationControllerPacket(
        int                                 id,
        PlayerAnimationController.Serialized serialized
) implements IClientboundPacket {

    public static final Type<ClientboundSyncAnimationControllerPacket> TYPE
            = IPacket.type(ThatSkyInteractions.location("animation/sync"));

    public static final StreamCodec<ByteBuf, ClientboundSyncAnimationControllerPacket> STREAM_CODEC
            = StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    ClientboundSyncAnimationControllerPacket::id,
                    PlayerAnimationController.STREAM_CODEC,
                    ClientboundSyncAnimationControllerPacket::serialized,
                    ClientboundSyncAnimationControllerPacket::new
            );

    public static ClientboundSyncAnimationControllerPacket of(
            final @NonNull LivingEntity     entity
    ) {
        return new ClientboundSyncAnimationControllerPacket(
                entity.getId(),
                PlayerAnimationSystem.getAnimationData(entity).getController().serialize()
        );
    }

    @Override
    public void handleOnClient(final @NonNull Player player) {

        final var entity        = player.level().getEntity(this.id());
        final var animatable    = PlayerAnimationSystem.tryParseAnimatable(entity);
        if (animatable != null) {

            final var attachment = PlayerAnimationSystem.getAnimationData(animatable);
            attachment.getController().deserialize(this.serialized());

            // just for in case
            animatable.yBodyRot = animatable.getYRot();

        }

    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
