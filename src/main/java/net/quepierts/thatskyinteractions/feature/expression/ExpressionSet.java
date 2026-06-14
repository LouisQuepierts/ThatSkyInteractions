package net.quepierts.thatskyinteractions.feature.expression;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.List;

public record ExpressionSet(
        Identifier icon,
        List<Expression> expressions
) {

    public static final Codec<ExpressionSet> CODEC
            = RecordCodecBuilder.create(instance -> instance.group(
                    Identifier.CODEC.fieldOf("icon").forGetter(ExpressionSet::icon),
                    Expression.CODEC.listOf(1, 16).fieldOf("expressions").forGetter(ExpressionSet::expressions)
            ).apply(instance, ExpressionSet::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ExpressionSet> STREAM_CODEC
            = StreamCodec.composite(
                    Identifier.STREAM_CODEC,
                    ExpressionSet::icon,
                    ByteBufCodecs.<RegistryFriendlyByteBuf, Expression>list().apply(Expression.STREAM_CODEC),
                    ExpressionSet::expressions,
                    ExpressionSet::new
            );

    public boolean leveled() {
        return this.expressions.size() > 1;
    }

    public int levels() {
        return this.expressions.size() == 1 ? 0 : this.expressions.size();
    }
}
