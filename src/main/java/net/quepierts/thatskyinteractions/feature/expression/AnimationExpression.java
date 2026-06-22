package net.quepierts.thatskyinteractions.feature.expression;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerAnimationDefinition;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerMask;
import net.quepierts.thatskyinteractions.core.animation.model.SourceDefinition;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.animation.event.PlayerAnimationControllerEvent;
import net.quepierts.thatskyinteractions.feature.animation.event.RegisterPlayerAnimationEvent;
import net.quepierts.thatskyinteractions.feature.control.PlayerControlSystem;
import net.quepierts.thatskyinteractions.feature.expression.runtime.AnimationExpressionState;
import net.quepierts.thatskyinteractions.feature.expression.runtime.ExpressionState;
import net.quepierts.thatskyinteractions.feature.registry.AnimationLayerTypes;
import net.quepierts.thatskyinteractions.feature.registry.ExpressionTypes;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AnimationExpression implements Expression {

    public static final String AUTO = "auto";
    private static final Identifier EMPTY = ThatSkyInteractions.location("empty");

    public static final MapCodec<AnimationExpression> MAP_CODEC
            = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.STRING.fieldOf("animation").forGetter(AnimationExpression::animation)
            ).apply(instance, AnimationExpression::new));

    public static final StreamCodec<ByteBuf, AnimationExpression> STREAM_CODEC
            = StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    AnimationExpression::animation,
                    AnimationExpression::new
            );

    private final String animation;

    private Identifier animationId = EMPTY;

    @Override
    public @NonNull ExpressionType<? extends Expression> getType() {
        return ExpressionTypes.ANIMATION.get();
    }

    @Override
    public void onPerform(@NonNull ServerPlayer player) {
        PlayerAnimationSystem.play(player, this.animationId);
        PlayerControlSystem.align(player);
    }

    @Override
    public void onInterrupt(@NonNull ServerPlayer player) {
        PlayerAnimationSystem.exit(player, AnimationLayerTypes.DEFAULT.getId());
    }

    @Override
    public boolean isInterruptible(
            final @NonNull Player           player,
            final @Nullable ExpressionState state
    ) {
        return false;
    }

    @Override
    public boolean isFinished(
            final @NonNull  Player                       player,
            final @Nullable ExpressionState              state
    ) {
        return (state instanceof AnimationExpressionState aState)
                && aState.getStatus() == AnimationExpressionState.Status.FINISHED;
    }

    @Override
    public void onRegisterPlayerAnimation(
            @NonNull RegisterPlayerAnimationEvent       event,
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
                "sequence",
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
    public void onGenerateData(@NonNull Identifier identifier, int level) {

        if (AUTO.equalsIgnoreCase(this.animation)) {
            this.animationId    = level != 0 ? identifier.withSuffix("_" + level) : identifier;
        } else {
            this.animationId    = Identifier.tryParse(this.animation);
        }

    }

    @Override
    public void onAnimationFinished(
            final @NonNull  Player                          player,
            final @Nullable ExpressionState                 state,
            final PlayerAnimationControllerEvent.Finished   event
    ) {

        if (!(state instanceof AnimationExpressionState aState)) {
            return;
        }

        if (event.getLayer() != AnimationLayerTypes.DEFAULT.get()) {
            return;
        }

        if (event.getAnimation().equals(this.animationId)) {

            aState.setStatus(AnimationExpressionState.Status.FINISHED);

        }

    }

    @Override
    public boolean immediate() {
        return false;
    }

    @Override
    public @NonNull ExpressionState createRuntimeData() {
        return new AnimationExpressionState();
    }

    private String animation() {
        return this.animation;
    }
}
