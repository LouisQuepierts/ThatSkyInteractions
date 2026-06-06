package net.quepierts.thatskyinteractions.feature.animation.packet;

import dev.anvilcraft.lib.v2.network.packet.IClientboundPacket;
import dev.anvilcraft.lib.v2.network.packet.IPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public record AnimationSignalPacket(
        UUID    uuid,
        int     signal
) implements IClientboundPacket {

    public static final Type<AnimationSignalPacket> TYPE
            = IPacket.type(ThatSkyInteractions.location("animation/signal"));

    public static final StreamCodec<ByteBuf, AnimationSignalPacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            AnimationSignalPacket::uuid,
            ByteBufCodecs.VAR_INT,
            AnimationSignalPacket::signal,
            AnimationSignalPacket::new
    );

    public static AnimationSignalPacket of(
            @NonNull Player         player,
            int                     signal
    ) {
        return new AnimationSignalPacket(
                player.getUUID(),
                signal
        );
    }

    @Override
    public void handleOnClient(final @NonNull Player player) {

        final var level         = player.level();
        final var target        = level.getPlayerByUUID(this.uuid());

        if (target == null) {
            return;
        }

        final var data          = PlayerAnimationSystem.getAnimationData(target);
        final var controller    = data.getController();

        if (!controller.isPlaying()) {
            return;
        }

        final var animation     = controller.getAnimation();
        final var state         = controller.getFsmState();
        animation               .event(state, this.signal());

    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
