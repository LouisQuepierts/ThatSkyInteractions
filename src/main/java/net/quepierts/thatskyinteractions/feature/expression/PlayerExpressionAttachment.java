package net.quepierts.thatskyinteractions.feature.expression;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.PriorityQueue;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import lombok.Getter;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.feature.expression.runtime.ExpressionState;
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

    private static final WeakReference<Expression> NULL
            = new WeakReference<>(null);

    private @Nullable   Pending                     pending;

    private @NonNull    WeakReference<Expression>   reference   = NULL;
    private @Nullable   Identifier                  current;
    private @Nullable   ExpressionState             state;

    public static PlayerExpressionAttachment getAttachment(@NonNull Player player) {
        return player.getData(AttachmentTypes.PLAYER_EXPRESSION);
    }

    public PlayerExpressionAttachment() { }

    public void enqueue(
            final @NonNull Identifier   identifier
    ) {
        this.enqueue(identifier, 0);
    }

    public void enqueue(
            final @NonNull Identifier   identifier,
            final          int          level
    ) {
        this.pending = Pending.of(identifier, level);
    }

    public @Nullable Pending dequeue() {
        final var pending = this.pending;
        this.pending = null;
        return pending;
    }

    public void start(
            final @NonNull Expression   expression,
            final @NonNull Identifier   identifier
    ) {
        this.current    = identifier;
        this.reference  = new WeakReference<>(expression);
        this.state      = expression.createRuntimeData();
    }

    public void clear() {
        this.reference  = NULL;
        this.current    = null;
        this.state      = null;
    }

    public boolean isExpressing() {
        return this.current != null;
    }

    public record Pending(
            @NonNull Identifier identifier,
                     int        level
    ) {

        private static Pending of(
                @NonNull Identifier identifier
        ) {
            return new Pending(identifier, 0);
        }

        private static Pending of(
                @NonNull Identifier identifier,
                         int        level
        ) {
            return new Pending(identifier, level);
        }

        public boolean leveled() {
            return this.level > 0;
        }

    }
}
