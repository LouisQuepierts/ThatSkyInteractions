package net.quepierts.thatskyinteractions.feature.expression;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.feature.animation.event.RegisterPlayerAnimationEvent;
import net.quepierts.thatskyinteractions.feature.interaction.PlayerInteractionAttachment;
import net.quepierts.thatskyinteractions.feature.interaction.PlayerInteractionSystem;
import net.quepierts.thatskyinteractions.feature.registry.TsiRegistries;
import org.jspecify.annotations.NonNull;

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

    default void onInterrupt(
            final @NonNull ServerPlayer                 player
    ) {}

    default boolean isInterruptible(
            final @NonNull Player                   player
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
            final @NonNull ServerPlayer                 player
    ) {
        return true;
    }

    default void onGenerateData(
            final @NonNull Identifier                   identifier,
            final          int                          level
    ) { }

    default boolean immediate() {
        return true;
    }

    default boolean hidden() {
        return true;
    }
}
