package net.quepierts.thatskyinteractions.feature.interaction;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.bond.PlayerBondSystem;
import net.quepierts.thatskyinteractions.feature.expression.event.RegisterExpressionEvent;
import net.quepierts.thatskyinteractions.feature.interaction.expression.RiderExpression;
import net.quepierts.thatskyinteractions.feature.registry.AnimationLayerTypes;
import net.quepierts.thatskyinteractions.feature.registry.InteractionTypes;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CarryInteraction implements Interaction, Expressional {

    public static final MapCodec<CarryInteraction> MAP_CODEC
            = RecordCodecBuilder.mapCodec(builder -> builder.group(
                    Identifier.CODEC.fieldOf("requester").forGetter(CarryInteraction::getRequester),
                    Identifier.CODEC.fieldOf("receiver").forGetter(CarryInteraction::getReceiver)
            ).apply(builder, CarryInteraction::new));

    public static final StreamCodec<ByteBuf, CarryInteraction> STREAM_CODEC
            = StreamCodec.composite(
                    Identifier.STREAM_CODEC,
                    CarryInteraction::getRequester,
                    Identifier.STREAM_CODEC,
                    CarryInteraction::getReceiver,
                    CarryInteraction::new
            );

    private final Identifier requester;
    private final Identifier receiver;

    @Getter
    private Identifier          receiverExpression  = DEFAULT_EXPRESSION_RECEIVER;

    @Override
    public @NonNull InteractionType<? extends Interaction> getType() {
        return InteractionTypes.CARRY.get();
    }

    @Override
    public void onInvite(
            final @NonNull  ServerPlayer        requester,
            final @NonNull  ServerPlayer        receiver
    ) {
        PlayerAnimationSystem.play(requester, this.requester, AnimationLayerTypes.DEFAULT.getId());
    }

    @Override
    public void onAccepted(
            final @NonNull  ServerPlayer        requester,
            final @NonNull  ServerPlayer        receiver
    ) {
        PlayerBondSystem.carry(requester, receiver);
        PlayerAnimationSystem.exit(requester, AnimationLayerTypes.DEFAULT.getId());
    }

    @Override
    public void onCancel(
            final @NonNull  ServerPlayer        requester,
            final @Nullable ServerPlayer        receiver
    ) {
        PlayerAnimationSystem.exit(requester, AnimationLayerTypes.DEFAULT.getId());
    }

    @Override
    public void onFinished(
            final @NonNull  ServerPlayer        requester,
            final @NonNull  ServerPlayer        receiver
    ) {
        PlayerAnimationSystem.exit(requester, AnimationLayerTypes.DEFAULT.getId());
    }

    @Override
    public void onGenerateData(
            final @NonNull Identifier           identifier,
            final          int                  level
    ) {
        final var subfix            = level != 0 ? ("_" + level) : "";
        final var path              = (identifier.getPath() + subfix);
        this.receiverExpression     = identifier.withPath("interaction/" + path + ".receiver");
    }

    @Override
    public void onRegisterExpression(
            final @NonNull RegisterExpressionEvent  event,
            final @NonNull Identifier               identifier,
            final          int                      level
    ) {
        event.register(this.receiverExpression, new RiderExpression(this.receiver));
    }
}
