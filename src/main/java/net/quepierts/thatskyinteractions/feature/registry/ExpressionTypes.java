package net.quepierts.thatskyinteractions.feature.registry;

import lombok.experimental.UtilityClass;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.expression.*;
import net.quepierts.thatskyinteractions.feature.interaction.expression.InteractionReceiverExpression;
import net.quepierts.thatskyinteractions.feature.interaction.expression.InteractionRequesterExpression;
import net.quepierts.thatskyinteractions.feature.registry.builer.ExpressionTypeBuilder;
import net.quepierts.thatskyinteractions.feature.registry.entry.ExpressionTypeEntry;

@UtilityClass
public class ExpressionTypes {

    public static final ExpressionTypeEntry<AnimationExpression> ANIMATION
            = ExpressionTypes.<AnimationExpression>type("animation")
            .codec(AnimationExpression.MAP_CODEC)
            .streamCodec(AnimationExpression.STREAM_CODEC)
            .register();

    public static final ExpressionTypeEntry<ContinuousAnimationExpression> CONTINUOUS_ANIMATION
            = ExpressionTypes.<ContinuousAnimationExpression>type("continuous_animation")
            .codec(ContinuousAnimationExpression.MAP_CODEC)
            .streamCodec(ContinuousAnimationExpression.STREAM_CODEC)
            .register();

    public static final ExpressionTypeEntry<InteractionRequesterExpression> INTERACTION_REQUESTER
            = ExpressionTypes.<InteractionRequesterExpression>type("interaction_requester")
            .codec(InteractionRequesterExpression.MAP_CODEC)
            .streamCodec(InteractionRequesterExpression.STREAM_CODEC)
            .register();

    public static final ExpressionTypeEntry<InteractionReceiverExpression> INTERACTION_RECEIVER
            = ExpressionTypes.<InteractionReceiverExpression>type("interaction_receiver")
            .codec(InteractionReceiverExpression.MAP_CODEC)
            .streamCodec(InteractionReceiverExpression.STREAM_CODEC)
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
