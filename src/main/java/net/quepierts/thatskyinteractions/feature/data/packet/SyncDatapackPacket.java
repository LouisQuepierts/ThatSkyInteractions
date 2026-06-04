package net.quepierts.thatskyinteractions.feature.data.packet;

import dev.anvilcraft.lib.v2.network.packet.IClientboundPacket;
import dev.anvilcraft.lib.v2.network.packet.IPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.data.DataSyncSystem;
import org.jspecify.annotations.NonNull;

@SuppressWarnings("unused")
public record SyncDatapackPacket(
        int         id,
        PacketCache cache
) implements IClientboundPacket {

    public static final Type<SyncDatapackPacket> TYPE
            = IPacket.type(ThatSkyInteractions.location("sync_datapack"));

    public static final StreamCodec<ByteBuf, SyncDatapackPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SyncDatapackPacket::id,
            PacketCache.STREAM_CODEC,
            SyncDatapackPacket::cache,
            SyncDatapackPacket::new
    );

    @Override
    public void handleOnClient(final @NonNull Player player) {
        DataSyncSystem.handle(this);
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
