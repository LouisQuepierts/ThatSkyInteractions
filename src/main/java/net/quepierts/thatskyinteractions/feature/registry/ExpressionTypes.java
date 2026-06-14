package net.quepierts.thatskyinteractions.feature.registry;

import lombok.experimental.UtilityClass;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.expression.AnimationExpression;
import net.quepierts.thatskyinteractions.feature.expression.Expression;
import net.quepierts.thatskyinteractions.feature.registry.builer.ExpressionTypeBuilder;
import net.quepierts.thatskyinteractions.feature.registry.entry.ExpressionTypeEntry;

@UtilityClass
public class ExpressionTypes {

    public static final ExpressionTypeEntry<AnimationExpression> ANIMATION
            = ExpressionTypes.<AnimationExpression>type("animation")
            .codec(AnimationExpression.MAP_CODEC)
            .streamCodec(AnimationExpression.STREAM_CODEC)
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
