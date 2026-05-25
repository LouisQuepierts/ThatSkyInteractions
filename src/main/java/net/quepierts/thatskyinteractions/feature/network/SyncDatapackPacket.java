package net.quepierts.thatskyinteractions.feature.network;

import dev.anvilcraft.lib.v2.network.packet.IClientboundPacket;
import dev.anvilcraft.lib.v2.network.packet.IPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationManager;
import net.quepierts.thatskyinteractions.feature.animation.bedrock.BedrockAnimationManager;
import org.jspecify.annotations.NonNull;

@SuppressWarnings("unused")
public record SyncDatapackPacket(
        String      datatype,
        PacketCache cache
) implements IClientboundPacket {

    public static final Type<SyncDatapackPacket> TYPE
            = IPacket.type(ThatSkyInteractions.location("sync_datapack"));

    public static final StreamCodec<ByteBuf, SyncDatapackPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public SyncDatapackPacket decode(final ByteBuf byteBuf) {
            var cache = new PacketCache();

            var datatype = ByteBufCodecs.STRING_UTF8.decode(byteBuf);
            cache.read(byteBuf);
            return new SyncDatapackPacket(datatype, cache);
        }

        @Override
        public void encode(final ByteBuf byteBuf, final SyncDatapackPacket syncDatapackPacket) {
            ByteBufCodecs.STRING_UTF8.encode(byteBuf, syncDatapackPacket.datatype());
            syncDatapackPacket.cache().write(byteBuf);
        }
    };

    @Override
    public void handleOnClient(final @NonNull Player player) {
        switch (this.datatype) {
            case "animation_source":
                BedrockAnimationManager.getInstance().sync(this.cache);
                break;
            case "animation_definition":
                PlayerAnimationManager.getInstance().sync(this.cache);
        }
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
