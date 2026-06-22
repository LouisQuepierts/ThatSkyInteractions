package net.quepierts.thatskyinteractions.feature.interaction.expression;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.feature.expression.Expression;
import net.quepierts.thatskyinteractions.feature.expression.ExpressionType;
import net.quepierts.thatskyinteractions.feature.expression.runtime.ExpressionState;
import net.quepierts.thatskyinteractions.feature.interaction.PlayerInteractionSystem;
import net.quepierts.thatskyinteractions.feature.registry.ExpressionTypes;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class DefaultReceiverExpression implements Expression {

    public static final DefaultReceiverExpression INSTANCE
            = new DefaultReceiverExpression();

    public static final MapCodec<DefaultReceiverExpression> MAP_CODEC
            = MapCodec.unit(INSTANCE);

    public static final StreamCodec<ByteBuf, DefaultReceiverExpression> STREAM_CODEC
            = StreamCodec.unit(INSTANCE);

    @Override
    public @NonNull ExpressionType<? extends Expression> getType() {
        return ExpressionTypes.INTERACTION_RECEIVER.get();
    }

    @Override
    public void onPerform(@NonNull ServerPlayer player) {
    }

    @Override
    public boolean isFinished(
            final @NonNull  Player                      player,
            final @Nullable ExpressionState             state
    ) {
        final var attachment = PlayerInteractionSystem.getInteractionAttachment(player);
        return attachment.getOngoing() == null;
    }

    @Override
    public boolean isInterruptible(
            final @NonNull Player           player,
            final @Nullable ExpressionState state
    ) {
        return false;
    }

    @Override
    public boolean immediate() {
        return false;
    }
}