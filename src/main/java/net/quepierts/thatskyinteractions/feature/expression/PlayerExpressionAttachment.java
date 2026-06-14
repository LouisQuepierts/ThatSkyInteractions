package net.quepierts.thatskyinteractions.feature.expression;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.feature.network.StreamCodecUtils;
import net.quepierts.thatskyinteractions.feature.registry.AttachmentTypes;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Getter
public final class PlayerExpressionAttachment {

    public static final Codec<PlayerExpressionAttachment> CODEC
            = MapCodec.unitCodec(PlayerExpressionAttachment::new);

    public static final StreamCodec<ByteBuf, PlayerExpressionAttachment> STREAM_CODEC
            = StreamCodecUtils.unit(PlayerExpressionAttachment::new);

    private Identifier current;
    private long startTime;

    public static PlayerExpressionAttachment getAttachment(@NonNull Player player) {
        return player.getData(AttachmentTypes.PLAYER_EXPRESSION);
    }

    public PlayerExpressionAttachment() { }

    public void start(@NonNull Identifier id, long gameTime) {
        this.current = id;
        this.startTime = gameTime;
    }

    public void clear() {
        this.current = null;
        this.startTime = 0;
    }

    public boolean isExpressing() {
        return this.current != null;
    }

    public @Nullable Identifier getCurrent() {
        return this.current;
    }
}
