package net.quepierts.thatskyinteractions.feature.network;

import dev.anvilcraft.lib.v2.network.packet.IClientboundPacket;
import dev.anvilcraft.lib.v2.network.packet.IPacket;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.model.animation.PlayerAnimationDefinition;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationManager;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationParser;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.function.IntFunction;

@SuppressWarnings("unused")
public record SyncAnimationDefinitionPacket(
        Map<Identifier, PlayerAnimationDefinition> definitions
) implements IClientboundPacket {

    public static final Type<SyncAnimationDefinitionPacket> TYPE
            = IPacket.type(ThatSkyInteractions.location("sync_animation_definition"));

    public static final StreamCodec<ByteBuf, SyncAnimationDefinitionPacket> STREAM_CODEC = ByteBufCodecs.map(
            (IntFunction<Map<Identifier, PlayerAnimationDefinition>>) Object2ObjectOpenHashMap::new,
            ByteBufCodecs.STRING_UTF8.map(
                    Identifier::parse,
                    Identifier::toString
            ),
            PlayerAnimationParser.ANIMATION_STREAM_CODEC
    ).map(
            SyncAnimationDefinitionPacket::new,
            SyncAnimationDefinitionPacket::definitions
    );

    @Override
    public void handleOnClient(final @NonNull Player player) {

        PlayerAnimationManager.getInstance().sync(this.definitions);

    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
