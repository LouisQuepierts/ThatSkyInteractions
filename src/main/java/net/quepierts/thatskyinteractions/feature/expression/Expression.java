package net.quepierts.thatskyinteractions.feature.expression;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.feature.animation.event.RegisterPlayerAnimationEvent;
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

    void onPerform(@NonNull ServerPlayer player);

    void onCancel(@NonNull ServerPlayer player);

    default void onFinished(@NonNull ServerPlayer player) { }

    default void onInterrupted(@NonNull ServerPlayer player) { }

    default void onClientPerform(@NonNull Player player) { }

    default void onRegisterPlayerAnimation(
            @NonNull RegisterPlayerAnimationEvent event,
            @NonNull Identifier identifier,
            int level
    ) { }

    default void onGenerateData(
            @NonNull Identifier identifier,
            int level
    ) { }

    default int duration() {
        return 0;
    }

    default boolean immediate() {
        return this.duration() == 0;
    }
}
