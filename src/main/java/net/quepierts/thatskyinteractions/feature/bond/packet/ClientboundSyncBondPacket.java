package net.quepierts.thatskyinteractions.feature.bond.packet;

import dev.anvilcraft.lib.v2.network.packet.IClientboundPacket;
import dev.anvilcraft.lib.v2.network.packet.IPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.bond.PlayerBondSystem;
import net.quepierts.thatskyinteractions.feature.bond.PlayerCarryRelation;
import net.quepierts.thatskyinteractions.feature.bond.PlayerHandholdRelation;
import org.jspecify.annotations.NonNull;

public record ClientboundSyncBondPacket(
        int                                 id,
        PlayerCarryRelation.Serialized      carry,
        PlayerHandholdRelation.Serialized   handhold
) implements IClientboundPacket {

    public static final Type<ClientboundSyncBondPacket> TYPE
            = IPacket.type(ThatSkyInteractions.location("bond/sync"));

    public static final StreamCodec<ByteBuf, ClientboundSyncBondPacket> STREAM_CODEC
            = StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    ClientboundSyncBondPacket::id,
                    PlayerCarryRelation.Serialized.STREAM_CODEC,
                    ClientboundSyncBondPacket::carry,
                    PlayerHandholdRelation.Serialized.STREAM_CODEC,
                    ClientboundSyncBondPacket::handhold,
                    ClientboundSyncBondPacket::new
            );

    public static ClientboundSyncBondPacket of(
            final @NonNull Player player
    ) {
        final var attachment = PlayerBondSystem.getAttachment(player);
        return new ClientboundSyncBondPacket(
                player.getId(),
                attachment.getCarry().serialize(),
                attachment.getHandhold().serialize()
        );
    }

    @Override
    public void handleOnClient(final @NonNull Player player) {

        final var level = player.level();
        if (!(level.getEntity(this.id()) instanceof Player target)) {
            return;
        }

        final var attachment = PlayerBondSystem.getAttachment(target);
        attachment.getCarry().deserialize(this.carry, level);
        attachment.getHandhold().deserialize(this.handhold, level);

        target.refreshDimensions();

    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
