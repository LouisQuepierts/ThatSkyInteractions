package net.quepierts.thatskyinteractions.feature.interaction.expression;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.quepierts.thatskyinteractions.feature.expression.Expression;
import net.quepierts.thatskyinteractions.feature.expression.ExpressionType;
import net.quepierts.thatskyinteractions.feature.registry.ExpressionTypes;
import org.jspecify.annotations.NonNull;

public interface InteractionExpression extends Expression {

    @NonNull InteractionExpression DUMMY = _ -> {};

    @NonNull MapCodec<InteractionExpression> MAP_CODEC = MapCodec.unit(DUMMY);

    @NonNull StreamCodec<ByteBuf, InteractionExpression> STREAM_CODEC = StreamCodec.unit(DUMMY);

    @Override
    default @NonNull ExpressionType<? extends Expression> getType() {
        // most interaction expression are register by system
        // they do not need serialization or synchronization
        return ExpressionTypes.INTERACTION.get();
    }

}
