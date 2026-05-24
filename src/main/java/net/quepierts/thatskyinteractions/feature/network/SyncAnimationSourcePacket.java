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
import net.quepierts.thatskyinteractions.core.model.animation.bedrock.BedrockAnimationDefinition;
import net.quepierts.thatskyinteractions.feature.animation.bedrock.BedrockAnimationManager;
import net.quepierts.thatskyinteractions.feature.animation.bedrock.BedrockAnimationParser;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.function.IntFunction;

@SuppressWarnings("unused")
public record SyncAnimationSourcePacket(
        Map<Identifier, BedrockAnimationDefinition> definitions
) implements IClientboundPacket {

    public static final Type<SyncAnimationSourcePacket> TYPE
            = IPacket.type(ThatSkyInteractions.location("sync_animation_source"));

    public static final StreamCodec<ByteBuf, SyncAnimationSourcePacket> STREAM_CODEC = ByteBufCodecs.map(
            (IntFunction<Map<Identifier, BedrockAnimationDefinition>>) Object2ObjectOpenHashMap::new,
            ByteBufCodecs.STRING_UTF8.map(
                    Identifier::parse,
                    Identifier::toString
            ),
            BedrockAnimationParser.ANIMATION_DEFINITION_STREAM_CODEC
    ).map(
            SyncAnimationSourcePacket::new,
            SyncAnimationSourcePacket::definitions
    );

    @Override
    public void handleOnClient(final @NonNull Player player) {

        BedrockAnimationManager.getInstance().sync(this.definitions);

    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
