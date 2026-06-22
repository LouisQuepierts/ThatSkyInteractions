package net.quepierts.thatskyinteractions.feature.expression;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerAnimationDefinition;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerMask;
import net.quepierts.thatskyinteractions.core.animation.model.SourceDefinition;
import net.quepierts.thatskyinteractions.feature.animation.event.RegisterPlayerAnimationEvent;
import net.quepierts.thatskyinteractions.feature.expression.runtime.AnimationExpressionState;
import net.quepierts.thatskyinteractions.feature.expression.runtime.ExpressionState;
import net.quepierts.thatskyinteractions.feature.registry.ExpressionTypes;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.Optional;

@Slf4j
public final class SequenceAnimationExpression extends AbstractAnimationExpression implements Expression {

    public static final MapCodec<SequenceAnimationExpression> MAP_CODEC
            = AbstractAnimationExpression.codec(SequenceAnimationExpression::new);

    public static final StreamCodec<ByteBuf, SequenceAnimationExpression> STREAM_CODEC
            = AbstractAnimationExpression.streamCodec(SequenceAnimationExpression::new);

    SequenceAnimationExpression(final String animation) {
        super(animation);
    }

    @Override
    public @NonNull ExpressionType<? extends Expression> getType() {
        return ExpressionTypes.ANIMATION.get();
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
    public @NonNull ExpressionState createRuntimeData() {
        return new AnimationExpressionState();
    }
}
