package net.quepierts.thatskyinteractions.feature.registry;

import lombok.experimental.UtilityClass;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.expression.*;
import net.quepierts.thatskyinteractions.feature.interaction.expression.DefaultReceiverExpression;
import net.quepierts.thatskyinteractions.feature.interaction.expression.DefaultRequesterExpression;
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

    public static final ExpressionTypeEntry<DefaultRequesterExpression> INTERACTION_REQUESTER
            = ExpressionTypes.<DefaultRequesterExpression>type("interaction_requester")
            .codec(DefaultRequesterExpression.MAP_CODEC)
            .streamCodec(DefaultRequesterExpression.STREAM_CODEC)
            .register();

    public static final ExpressionTypeEntry<DefaultReceiverExpression> INTERACTION_RECEIVER
            = ExpressionTypes.<DefaultReceiverExpression>type("interaction_receiver")
            .codec(DefaultReceiverExpression.MAP_CODEC)
            .streamCodec(DefaultReceiverExpression.STREAM_CODEC)
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
