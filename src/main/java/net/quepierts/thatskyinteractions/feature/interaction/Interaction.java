package net.quepierts.thatskyinteractions.feature.interaction;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.quepierts.thatskyinteractions.feature.animation.event.RegisterPlayerAnimationEvent;
import net.quepierts.thatskyinteractions.feature.registry.TsiRegistries;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

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

    default boolean positional() {
        return true;
    }

    default int duration() {
        return 0;
    }

    default boolean immediate() {
        return this.duration() == 0;
    }

}
