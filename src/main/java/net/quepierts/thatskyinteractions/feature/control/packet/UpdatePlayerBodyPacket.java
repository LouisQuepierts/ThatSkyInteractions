package net.quepierts.thatskyinteractions.feature.control.packet;

import dev.anvilcraft.lib.v2.network.packet.IPacket;
import dev.anvilcraft.lib.v2.network.packet.ISensitiveBiPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import org.jspecify.annotations.NonNull;

public record UpdatePlayerBodyPacket(
        int     id,
        float   yBodyRot
) implements ISensitiveBiPacket {

    public static final Type<UpdatePlayerBodyPacket> TYPE
            = IPacket.type(ThatSkyInteractions.location("control/update_player_body"));

    public static final StreamCodec<ByteBuf, UpdatePlayerBodyPacket> STREAM_CODEC
            = StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    UpdatePlayerBodyPacket::id,
                    ByteBufCodecs.FLOAT,
                    UpdatePlayerBodyPacket::yBodyRot,
                    UpdatePlayerBodyPacket::new
            );

    public static UpdatePlayerBodyPacket client(
            @NonNull Player player
    ) {
        return new UpdatePlayerBodyPacket(
                player.getId(),
                player.yBodyRot
        );
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handleOnClient(final @NonNull Player player) {
        final var entity = player.level().getEntity(this.id());
        if (!(entity instanceof Player target)) {
            return;
        }

        target.yBodyRot     = this.yBodyRot();
    }

    @Override
    public void handleOnServer(final @NonNull Player player) {
        player.yBodyRot     = this.yBodyRot();

        final var level     = player.level();
        final var position  = player.position();
        PacketDistributor.sendToPlayersNear(
                (ServerLevel) level,
                (ServerPlayer) player,
                position.x(),
                position.y(),
                position.z(),
                64.0,
                this
        );
    }
}
