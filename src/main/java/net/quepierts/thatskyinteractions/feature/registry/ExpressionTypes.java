package net.quepierts.thatskyinteractions.feature.registry;

import lombok.experimental.UtilityClass;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.expression.*;
import net.quepierts.thatskyinteractions.feature.interaction.expression.DefaultInteractionExpression;
import net.quepierts.thatskyinteractions.feature.interaction.expression.InteractionExpression;
import net.quepierts.thatskyinteractions.feature.registry.builer.ExpressionTypeBuilder;
import net.quepierts.thatskyinteractions.feature.registry.entry.ExpressionTypeEntry;

@UtilityClass
public class ExpressionTypes {

    public static final ExpressionTypeEntry<SequenceAnimationExpression> ANIMATION
            = ExpressionTypes.<SequenceAnimationExpression>type("animation")
            .codec(SequenceAnimationExpression.MAP_CODEC)
            .streamCodec(SequenceAnimationExpression.STREAM_CODEC)
            .register();

    public static final ExpressionTypeEntry<ContinuousAnimationExpression> CONTINUOUS_ANIMATION
            = ExpressionTypes.<ContinuousAnimationExpression>type("continuous_animation")
            .codec(ContinuousAnimationExpression.MAP_CODEC)
            .streamCodec(ContinuousAnimationExpression.STREAM_CODEC)
            .register();

    public static final ExpressionTypeEntry<InteractionExpression> INTERACTION
            = ExpressionTypes.<InteractionExpression>type("interaction")
            .codec(InteractionExpression.MAP_CODEC)
            .streamCodec(InteractionExpression.STREAM_CODEC)
            .register();

    public static void register() { }

    private static <E extends Expression> ExpressionTypeBuilder<E> type(String name) {
        return ThatSkyInteractions.REGISTRUM.entry(
                name,
                callback -> new ExpressionTypeBuilder<>(
                        ThatSkyInteractions.REGISTRUM,
                        ThatSkyInteractions.REGISTRUM,
                        name,
                        callback
                )
        );
    }
}
