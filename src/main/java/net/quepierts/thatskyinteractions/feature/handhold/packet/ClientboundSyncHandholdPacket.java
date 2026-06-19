package net.quepierts.thatskyinteractions.feature.handhold.packet;

import dev.anvilcraft.lib.v2.network.packet.IClientboundPacket;
import dev.anvilcraft.lib.v2.network.packet.IPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.handhold.PlayerHandholdRelation;
import net.quepierts.thatskyinteractions.feature.handhold.PlayerHandholdingSystem;
import org.jspecify.annotations.NonNull;

public record ClientboundSyncHandholdPacket(
        int                                 id,
        PlayerHandholdRelation.Serialized   serialized
) implements IClientboundPacket {

    public static final Type<ClientboundSyncHandholdPacket> TYPE
            = IPacket.type(ThatSkyInteractions.location("handhold/sync"));

    public static final StreamCodec<ByteBuf, ClientboundSyncHandholdPacket> STREAM_CODEC
            = StreamCodec.composite(
                    ByteBufCodecs.INT,
                    ClientboundSyncHandholdPacket::id,
                    PlayerHandholdRelation.Serialized.STREAM_CODEC,
                    ClientboundSyncHandholdPacket::serialized,
                    ClientboundSyncHandholdPacket::new
            );

    public static ClientboundSyncHandholdPacket of(
            final @NonNull Player player
    ) {
        final var attachment = PlayerHandholdingSystem.getAttachment(player);
        final var serialize = attachment.getRelation().serialize();
        return new ClientboundSyncHandholdPacket(player.getId(), serialize);
    }

    @Override
    public void handleOnClient(final @NonNull Player player) {

        final var level = player.level();
        if (!(level.getEntity(this.id()) instanceof Player target)) {
            return;
        }

        final var attachment = PlayerHandholdingSystem.getAttachment(target);
        attachment.getRelation().deserialize(this.serialized, level);

    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
