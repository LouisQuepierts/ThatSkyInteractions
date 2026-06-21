package net.quepierts.thatskyinteractions.feature.interaction;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.expression.InteractionRequesterExpression;
import net.quepierts.thatskyinteractions.feature.expression.event.RegisterExpressionEvent;
import net.quepierts.thatskyinteractions.feature.handhold.PlayerHandholdingSystem;
import net.quepierts.thatskyinteractions.feature.registry.AnimationLayerTypes;
import net.quepierts.thatskyinteractions.feature.registry.InteractionTypes;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class HoldingHandsInteraction implements Interaction {

    public static final Identifier                  INVITE      = ThatSkyInteractions.location("holding_hands_invite");
    private static final HoldingHandsInteraction    INSTANCE    = new HoldingHandsInteraction();

    public static final MapCodec<HoldingHandsInteraction> MAP_CODEC
            = MapCodec.unit(INSTANCE);

    public static final StreamCodec<ByteBuf, HoldingHandsInteraction> STREAM_CODEC
            = StreamCodec.unit(INSTANCE);

    @Override
    public @NonNull InteractionType<? extends Interaction> getType() {
        return InteractionTypes.HANDHOLD.get();
    }

    @Override
    public void onInvite(
            final @NonNull ServerPlayer requester,
            final @NonNull ServerPlayer receiver
    ) {
        PlayerAnimationSystem.play(requester, INVITE);
    }

    @Override
    public void onAccepted(
            final @NonNull ServerPlayer requester,
            final @NonNull ServerPlayer receiver
    ) {
        PlayerAnimationSystem.exit(requester, AnimationLayerTypes.DEFAULT.getId());
        PlayerHandholdingSystem.hold(requester, receiver);
    }

    @Override
    public void onCancel(
            final @NonNull ServerPlayer requester,
            final @Nullable ServerPlayer receiver
    ) {
        PlayerAnimationSystem.exit(requester, AnimationLayerTypes.DEFAULT.getId());
    }

    @Override
    public boolean positional() {
        return false;
    }
}
