package net.quepierts.thatskyinteractions.feature.network;

import dev.anvilcraft.lib.v2.network.packet.IClientboundPacket;
import dev.anvilcraft.lib.v2.network.packet.IPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.entity.AvatarExtension;
import org.jspecify.annotations.NonNull;

@SuppressWarnings("unused")
public record PlayAnimationPacket(
        Identifier  animation,
        int         id
) implements IClientboundPacket {

    public static final Type<PlayAnimationPacket> TYPE
            = IPacket.type(ThatSkyInteractions.location("play_animation"));

    public static final StreamCodec<ByteBuf, PlayAnimationPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.map(
                    Identifier::parse,
                    Identifier::toString
            ),
            PlayAnimationPacket::animation,
            ByteBufCodecs.VAR_INT,
            PlayAnimationPacket::id,
            PlayAnimationPacket::new
    );

    @Override
    public void handleOnClient(final @NonNull Player player) {

        final var level = player.level();
        final var entity = level.getEntity(this.id);

        if (entity instanceof AvatarExtension extension) {
            final var state = extension.a4j$GetAnimationState();
            state.play(this.animation);
        }

    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
