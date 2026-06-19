package net.quepierts.thatskyinteractions.feature.interaction;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.interaction.DefaultInteractionFSM;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.expression.InteractionReceiverExpression;
import net.quepierts.thatskyinteractions.feature.expression.InteractionRequesterExpression;
import net.quepierts.thatskyinteractions.feature.expression.event.RegisterExpressionEvent;
import net.quepierts.thatskyinteractions.feature.registry.InteractionTypes;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class UnlockInteraction implements Interaction {

    public static final Identifier INVITE               = ThatSkyInteractions.location("unlock_invite");
    public static final Identifier ACCEPT               = ThatSkyInteractions.location("unlock_accept");
    private static final UnlockInteraction INSTANCE     = new UnlockInteraction();

    public static final MapCodec<UnlockInteraction> MAP_CODEC
            = MapCodec.unit(INSTANCE);

    public static final StreamCodec<ByteBuf, UnlockInteraction> STREAM_CODEC
            = StreamCodec.unit(INSTANCE);

    private UnlockInteraction() { }

    @Override
    public @NonNull InteractionType<UnlockInteraction> getType() {
        return InteractionTypes.UNLOCK.get();
    }

    @Override
    public void onInvite(
            final @NonNull  ServerPlayer requester,
            final @NonNull  ServerPlayer receiver
    ) {

        PlayerAnimationSystem.play(
                requester,
                INVITE
        );

    }

    @Override
    public void onAccepted(
            final @NonNull  ServerPlayer requester,
            final @NonNull  ServerPlayer receiver
    ) {

        PlayerAnimationSystem.play(
                receiver,
                ACCEPT
        );

        PlayerAnimationSystem.signal(
                requester,
                DefaultInteractionFSM.REQUESTER_ACCEPT
        );

    }

    @Override
    public void onCancel(
            final @NonNull  ServerPlayer requester,
            final @Nullable ServerPlayer receiver
    ) {

        PlayerAnimationSystem.exit(requester);

    }

    @Override
    public boolean positional() {
        return false;
    }
}
