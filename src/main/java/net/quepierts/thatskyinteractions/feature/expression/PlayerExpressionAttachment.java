package net.quepierts.thatskyinteractions.feature.expression;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.feature.network.StreamCodecUtils;
import net.quepierts.thatskyinteractions.feature.registry.AttachmentTypes;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.ref.WeakReference;

@Getter
public final class PlayerExpressionAttachment {

    public static final Codec<PlayerExpressionAttachment> CODEC
            = MapCodec.unitCodec(PlayerExpressionAttachment::new);

    public static final StreamCodec<ByteBuf, PlayerExpressionAttachment> STREAM_CODEC
            = StreamCodecUtils.unit(PlayerExpressionAttachment::new);

    private WeakReference<Expression>   reference;
    private Identifier                  current;

    public static PlayerExpressionAttachment getAttachment(@NonNull Player player) {
        return player.getData(AttachmentTypes.PLAYER_EXPRESSION);
    }

    public PlayerExpressionAttachment() { }

    public void start(
            final @NonNull Expression   expression,
            final @NonNull Identifier   identifier
    ) {
        this.current = identifier;
        this.reference = new WeakReference<>(expression);
    }

    public void clear() {
        this.current = null;
        this.reference = null;
    }

    public boolean isExpressing() {
        return this.current != null;
    }

    public @Nullable Identifier getCurrent() {
        return this.current;
    }
}
