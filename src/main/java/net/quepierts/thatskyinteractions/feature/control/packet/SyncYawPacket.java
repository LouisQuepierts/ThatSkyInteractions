package net.quepierts.thatskyinteractions.feature.control.packet;

import dev.anvilcraft.lib.v2.network.packet.IClientboundPacket;
import dev.anvilcraft.lib.v2.network.packet.IPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public record SyncYawPacket(
        UUID    player,
        float   yaw
) implements IClientboundPacket {

    public static final Type<SyncYawPacket> TYPE
            = IPacket.type(ThatSkyInteractions.location("control/align_yaw"));

    public static final StreamCodec<ByteBuf, SyncYawPacket> STREAM_CODEC
            = StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC,
                    SyncYawPacket::player,
                    ByteBufCodecs.FLOAT,
                    SyncYawPacket::yaw,
                    SyncYawPacket::new
            );

    public static SyncYawPacket of(
            final @NonNull  Player  player
    ) {
        return new SyncYawPacket(
                player.getUUID(),
                player.getYRot()
        );
    }

    @Override
    public void handleOnClient(final @NonNull Player player) {
        if (player.level().getPlayerByUUID(this.player) instanceof Player target) {
            target.setYRot(this.yaw);
            target.setYHeadRot(this.yaw);
        }
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
