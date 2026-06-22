package net.quepierts.thatskyinteractions.feature.expression;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftFSM;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerAnimationDefinition;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerMask;
import net.quepierts.thatskyinteractions.core.animation.model.SourceDefinition;
import net.quepierts.thatskyinteractions.feature.animation.event.PlayerAnimationControllerEvent;
import net.quepierts.thatskyinteractions.feature.animation.event.RegisterPlayerAnimationEvent;
import net.quepierts.thatskyinteractions.feature.expression.runtime.AnimationExpressionState;
import net.quepierts.thatskyinteractions.feature.expression.runtime.ExpressionState;
import net.quepierts.thatskyinteractions.feature.registry.ExpressionTypes;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

@Slf4j
public final class ContinuousAnimationExpression extends AbstractAnimationExpression {

    public static final String AUTO = "auto";
    private static final Identifier EMPTY = ThatSkyInteractions.location("empty");

    public static final MapCodec<ContinuousAnimationExpression> MAP_CODEC
            = AbstractAnimationExpression.codec(ContinuousAnimationExpression::new);

    public static final StreamCodec<ByteBuf, ContinuousAnimationExpression> STREAM_CODEC
            = AbstractAnimationExpression.streamCodec(ContinuousAnimationExpression::new);

    ContinuousAnimationExpression(final String animation) {
        super(animation);
    }

    @Override
    public @NonNull ExpressionType<? extends Expression> getType() {
        return ExpressionTypes.CONTINUOUS_ANIMATION.get();
    }

    @Override
    public boolean isInterruptible(
            final @NonNull  Player                  player,
            final @Nullable ExpressionState         state
    ) {
        return (state != null)
                && ((AnimationExpressionState) state).getStatus() == AnimationExpressionState.Status.RUNNING;
    }

    @Override
    public void onRegisterPlayerAnimation(
            @NonNull RegisterPlayerAnimationEvent event,
            @NonNull Identifier                         identifier,
            int                                level
    ) {

        if (!AUTO.equalsIgnoreCase(this.animation)) {
            return;
        }

        final var typename = level != 0 ? identifier.withSuffix("_" + level) : identifier;

        final var prefix = typename.toString();
        final var namespace = Optional.<String>empty();
        final var sources = Map.of(
                "enter", new SourceDefinition(prefix + ".enter", 0.25f, 0.0f, namespace),
                "main", new SourceDefinition(prefix + ".main", 0.0f, 0.0f, namespace),
                "exit", new SourceDefinition(prefix + ".exit", 0.0f, 0.25f, namespace)
        );

        final var definition = new PlayerAnimationDefinition(
                "continuous",
                "thatskyinteractions:modified",
                sources,
                PlayerMask.empty(),
                false,
                true,
                false,
                PlayerAnimationDefinition.DEFAULT_LAYER
        );

        event.register(identifier, definition);
    }

    @Override
    public void onAnimationTransitionStart(
            final @NonNull  Player                                                  player,
            final @Nullable ExpressionState                                         state,
            final           PlayerAnimationControllerEvent.State.TransitionStart    event
    ) {

        if (state == null) {
            return;
        }

        final var currentState = event.getCurrentState();
        if (currentState == DefaultMinecraftFSM.CONTINUOUS_MAIN) {
            ((AnimationExpressionState) state).setStatus(AnimationExpressionState.Status.RUNNING);
        } else if (currentState == DefaultMinecraftFSM.CONTINUOUS_EXIT) {
            ((AnimationExpressionState) state).setStatus(AnimationExpressionState.Status.TRANSITING);
        }

    }

    @Override
    public @NonNull ExpressionState createRuntimeData() {
        final var state = new AnimationExpressionState();
        state.setStatus(AnimationExpressionState.Status.TRANSITING);
        return state;
    }
}
