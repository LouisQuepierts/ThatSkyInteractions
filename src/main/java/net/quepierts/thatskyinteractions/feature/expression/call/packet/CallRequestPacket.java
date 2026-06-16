package net.quepierts.thatskyinteractions.feature.expression.call.packet;

import dev.anvilcraft.lib.v2.network.packet.IPacket;
import dev.anvilcraft.lib.v2.network.packet.IServerboundPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.expression.call.PlayerCallSystem;
import org.jspecify.annotations.NonNull;

public record CallRequestPacket(
        // parameters
) implements IServerboundPacket {

    public static final CallRequestPacket INSTANCE = new CallRequestPacket();

    public static final Type<CallRequestPacket> TYPE
            = IPacket.type(ThatSkyInteractions.location("call/request"));

    public static final StreamCodec<ByteBuf, CallRequestPacket> STREAM_CODEC
            = StreamCodec.unit(INSTANCE);

    public static CallRequestPacket request() {
        return INSTANCE;
    }

    @Override
    public void handleOnServer(final @NonNull Player player) {
        PlayerCallSystem.call((ServerPlayer) player);
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
