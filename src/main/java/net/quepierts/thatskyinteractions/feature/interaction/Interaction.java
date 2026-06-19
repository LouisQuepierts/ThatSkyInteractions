package net.quepierts.thatskyinteractions.feature.interaction;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.feature.animation.event.PlayerAnimationControllerEvent;
import net.quepierts.thatskyinteractions.feature.animation.event.RegisterPlayerAnimationEvent;
import net.quepierts.thatskyinteractions.feature.expression.event.RegisterExpressionEvent;
import net.quepierts.thatskyinteractions.feature.registry.TsiRegistries;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public interface Interaction {

    Codec<Interaction> CODEC
            = TsiRegistries.INTERACTION_TYPE
            .byNameCodec()
            .dispatch(Interaction::getType, InteractionType::codec);

    StreamCodec<RegistryFriendlyByteBuf, Interaction> STREAM_CODEC
            = ByteBufCodecs.registry(TsiRegistries.Keys.INTERACTION_TYPE)
            .dispatch(Interaction::getType, InteractionType::streamCodec);

    @NonNull InteractionType<? extends Interaction> getType();

    void onInvite(
            final @NonNull  ServerPlayer            requester,
            final @NonNull  ServerPlayer            receiver
    );

    void onAccepted(
            final @NonNull  ServerPlayer            requester,
            final @NonNull  ServerPlayer            receiver
    );

    void onCancel(
            final @NonNull  ServerPlayer            requester,
            final @Nullable ServerPlayer            receiver
    );

    default void onFinished(
            final @NonNull  ServerPlayer            requester,
            final @NonNull  ServerPlayer            receiver
    ) { }

    default boolean isFinished(
            final @NonNull  Player                  player
    ) {
        return true;
    }

    default void onInterrupted(
            final @NonNull  ServerPlayer            requester,
            final @NonNull  ServerPlayer            receiver
    ) { }

    default void onRegisterPlayerAnimation(
            final @NonNull RegisterPlayerAnimationEvent event,
            final @NonNull Identifier                   identifier,
            final          int                          level
    ) { }

    default void onGenerateData(
            final @NonNull Identifier                   identifier,
            final          int                          level
    ) { }

    default void onAnimationFinished(
            final @NonNull ServerPlayer                     player,
            final PlayerAnimationControllerEvent.Finished   event
    ) { }

    default boolean positional() {
        return true;
    }

    default int duration() {
        return 0;
    }

    default boolean is(final @NonNull Interaction other) {
        return this == other;
    }

    default boolean is(final @NonNull InteractionType<?> type) {
        return this.getType() == type;
    }

    default <E extends Interaction> boolean is(final @NonNull Supplier<InteractionType<E>> supplier) {
        return this.getType() == supplier.get();
    }

}
