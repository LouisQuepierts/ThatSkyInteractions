package net.quepierts.thatskyinteractions.feature.control.packet;

import dev.anvilcraft.lib.v2.network.packet.IClientboundPacket;
import dev.anvilcraft.lib.v2.network.packet.IPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.animation.tween.PhysicalTweenAttachment;
import net.quepierts.thatskyinteractions.feature.utils.TsiInterpolators;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Eases;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public record AlignBodyPacket(
        UUID player
) implements IClientboundPacket {

    public static final Type<AlignBodyPacket> TYPE
            = IPacket.type(ThatSkyInteractions.location("control/align_body"));

    public static final StreamCodec<ByteBuf, AlignBodyPacket> STREAM_CODEC
            = StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC,
                    AlignBodyPacket::player,
                    AlignBodyPacket::new
            );

    public static AlignBodyPacket of(final @NonNull Player player) {
        return new AlignBodyPacket(player.getUUID());
    }

    @Override
    public void handleOnClient(final @NonNull Player player) {
        final var difference = Mth.degreesDifference(player.yBodyRot, player.getYHeadRot());

        PhysicalTweenAttachment.getAttachment(player.level()).tween().to(
                player::setYBodyRot,
                player.yBodyRot,
                player.getYHeadRot(),
                Mth.abs(difference) * 0.01f,
                TsiInterpolators.DEGREE,
                Eases.CUBIC_OUT
        );
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
