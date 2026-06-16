package net.quepierts.thatskyinteractions.feature.expression;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.feature.expression.call.PlayerVoiceType;
import net.quepierts.thatskyinteractions.feature.expression.call.PlayerVoiceTypeManager;
import net.quepierts.thatskyinteractions.feature.registry.AttachmentTypes;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Getter
public final class PlayerPreferenceAttachment {

    public static final MapCodec<PlayerPreferenceAttachment> MAP_CODEC
            = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Identifier.CODEC.optionalFieldOf("voice", PlayerVoiceType.DEFAULT_ID).forGetter(PlayerPreferenceAttachment::getVoice)
            ).apply(instance, PlayerPreferenceAttachment::new));

    public static final StreamCodec<ByteBuf, PlayerPreferenceAttachment> STREAM_CODEC
            = StreamCodec.composite(
                    Identifier.STREAM_CODEC,
                    PlayerPreferenceAttachment::getVoice,
                    PlayerPreferenceAttachment::new
            );

    public static PlayerPreferenceAttachment getAttachment(final @NonNull Player player) {
        return player.getData(AttachmentTypes.PLAYER_PREFERENCE);
    }

    private @NonNull Identifier voice;

    public PlayerPreferenceAttachment() {
        this.voice = PlayerVoiceType.DEFAULT_ID;
    }

    public void setVoice(
            final @Nullable Identifier voice
    ) {
        this.voice = voice == null || voice.equals(PlayerVoiceType.DEFAULT_ID)
                ? PlayerVoiceType.DEFAULT_ID
                : voice;
    }

    private PlayerPreferenceAttachment(
            final @NonNull Identifier voice
    ) {
        this.voice = voice.equals(PlayerVoiceType.DEFAULT_ID)
                ? PlayerVoiceType.DEFAULT_ID
                : voice;
    }

    public boolean shouldSerialize() {
        return this.voice != PlayerVoiceType.DEFAULT_ID;
    }

    public @NonNull PlayerVoiceType getVoiceType() {
        return PlayerVoiceTypeManager.getInstance().getNonNull(this.voice);
    }

}
