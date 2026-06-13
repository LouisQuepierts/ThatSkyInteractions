package net.quepierts.thatskyinteractions.feature.handhold.packet;

import dev.anvilcraft.lib.v2.network.packet.IPacket;
import dev.anvilcraft.lib.v2.network.packet.IServerboundPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.handhold.PlayerHandholdingSystem;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.UUID;

public record UnholdRequestPacket(
        Optional<UUID>  other
) implements IServerboundPacket {

    public static final Type<UnholdRequestPacket> TYPE
            = IPacket.type(ThatSkyInteractions.location("handhold/unhold"));

    public static final StreamCodec<ByteBuf, UnholdRequestPacket> STREAM_CODEC
            = StreamCodec.composite(
                    ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC),
                    UnholdRequestPacket::other,
                    UnholdRequestPacket::new
            );

    public static UnholdRequestPacket all() {
        return new UnholdRequestPacket(Optional.empty());
    }

    public static UnholdRequestPacket other(
            final @NonNull Player other
    ) {
        return new UnholdRequestPacket(Optional.of(other.getUUID()));
    }

    @Override
    public void handleOnServer(final @NonNull Player player) {

        if (this.other().isPresent()) {
            final var other = player.level().getPlayerByUUID(this.other().get());

            if (other instanceof ServerPlayer sp) {
                PlayerHandholdingSystem.unhold((ServerPlayer) player, sp);
            }

        } else {
            PlayerHandholdingSystem.unholdAll((ServerPlayer) player);
        }

    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
