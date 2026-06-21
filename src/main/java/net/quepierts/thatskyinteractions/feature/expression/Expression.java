package net.quepierts.thatskyinteractions.feature.expression;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.feature.animation.event.PlayerAnimationControllerEvent;
import net.quepierts.thatskyinteractions.feature.animation.event.RegisterPlayerAnimationEvent;
import net.quepierts.thatskyinteractions.feature.expression.runtime.ExpressionState;
import net.quepierts.thatskyinteractions.feature.registry.TsiRegistries;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface Expression {

    Codec<Expression> CODEC
            = TsiRegistries.EXPRESSION_TYPE
            .byNameCodec()
            .dispatch(Expression::getType, ExpressionType::codec);

    StreamCodec<RegistryFriendlyByteBuf, Expression> STREAM_CODEC
            = ByteBufCodecs.registry(TsiRegistries.Keys.EXPRESSION_TYPE)
            .dispatch(Expression::getType, ExpressionType::streamCodec);

    @NonNull ExpressionType<? extends Expression> getType();

    void onPerform(
            final @NonNull ServerPlayer                 player
    );

    default void onCancel(
            final @NonNull ServerPlayer                 player
    ) {}

    default void onInterrupt(
            final @NonNull ServerPlayer                 player
    ) {
        this.onCancel(player);
    }

    default boolean isInterruptible(
            final @NonNull  Player                      player,
            final @Nullable ExpressionState             state
    ) {
        return true;
    }

    default void onFinished(
            final @NonNull ServerPlayer                 player
    ) { }

    default void onClientPerform(
            final @NonNull Player                       player
    ) { }

    default void onRegisterPlayerAnimation(
            final @NonNull RegisterPlayerAnimationEvent event,
            final @NonNull Identifier                   identifier,
            final          int                          level
    ) { }

    default boolean isFinished(
            final @NonNull  Player                       player,
            final @Nullable ExpressionState              state
    ) {
        return this.immediate();
    }

    default void onGenerateData(
            final @NonNull Identifier                   identifier,
            final          int                          level
    ) { }

    default void onAnimationFinished(
            final @NonNull  Player                                          player,
            final @Nullable ExpressionState                                 state,
            final PlayerAnimationControllerEvent.Finished                   event
    ) { }

    default void onAnimationTransitionStart(
            final @NonNull Player                                           player,
            final @Nullable ExpressionState                                 state,
            final PlayerAnimationControllerEvent.State.TransitionStart      event
    ) { }

    default void onAnimationTransitionEnd(
            final @NonNull Player                                           player,
            final @Nullable ExpressionState                                 state,
            final PlayerAnimationControllerEvent.State.TransitionEnd        event
    ) { }

    default void onAnimationLooped(
            final @NonNull Player                                           player,
            final @Nullable ExpressionState                                 state,
            final PlayerAnimationControllerEvent.State.Loop                 event
    ) { }

    default boolean immediate() {
        return true;
    }

    default boolean hidden() {
        return true;
    }

    default @Nullable ExpressionState createRuntimeData() {
        return null;
    }
}
