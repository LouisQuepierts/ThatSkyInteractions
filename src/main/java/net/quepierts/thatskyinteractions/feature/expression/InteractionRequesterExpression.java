package net.quepierts.thatskyinteractions.feature.expression;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.feature.expression.runtime.ExpressionState;
import net.quepierts.thatskyinteractions.feature.interaction.PlayerInteractionSystem;
import net.quepierts.thatskyinteractions.feature.registry.ExpressionTypes;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class InteractionRequesterExpression implements Expression {

    public static final InteractionRequesterExpression INSTANCE
            = new InteractionRequesterExpression();

    public static final MapCodec<InteractionRequesterExpression> MAP_CODEC
            = MapCodec.unit(INSTANCE);

    public static final StreamCodec<ByteBuf, InteractionRequesterExpression> STREAM_CODEC
            = StreamCodec.unit(INSTANCE);

    @Override
    public @NonNull ExpressionType<? extends Expression> getType() {
        return ExpressionTypes.INTERACTION_REQUESTER.get();
    }

    @Override
    public void onPerform(@NonNull ServerPlayer player) {
    }

    @Override
    public void onCancel(final @NonNull ServerPlayer player) {
        PlayerInteractionSystem.cancel(player);
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