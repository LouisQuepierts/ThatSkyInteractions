package net.quepierts.thatskyinteractions.feature.control.packet;

import dev.anvilcraft.lib.v2.network.packet.IClientboundPacket;
import dev.anvilcraft.lib.v2.network.packet.IPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.control.PlayerNavigator;
import org.jspecify.annotations.NonNull;

public record NavigatePacket(
        Vec3 walkTarget,
        Vec3 lookTarget
) implements IClientboundPacket {

    public static final Type<NavigatePacket> TYPE
            = IPacket.type(ThatSkyInteractions.location("control/navigate"));

    public static final StreamCodec<ByteBuf, NavigatePacket> STREAM_CODEC
            = StreamCodec.composite(
                    Vec3.STREAM_CODEC,
                    NavigatePacket::walkTarget,
                    Vec3.STREAM_CODEC,
                    NavigatePacket::lookTarget,
                    NavigatePacket::new
            );

    @Override
    public void handleOnClient(final @NonNull Player player) {
        final var navigator = PlayerNavigator.get(player);
        navigator.to(this.walkTarget(), this.lookTarget());
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
