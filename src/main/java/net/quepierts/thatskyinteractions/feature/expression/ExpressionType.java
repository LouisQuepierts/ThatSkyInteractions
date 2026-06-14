package net.quepierts.thatskyinteractions.feature.expression;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.NonNull;

public record ExpressionType<T extends Expression>(
        @NonNull MapCodec<T> codec,
        @NonNull StreamCodec<ByteBuf, T> streamCodec
) { }
