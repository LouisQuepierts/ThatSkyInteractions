package net.quepierts.thatskyinteractions.feature.interaction;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.NonNull;

public record InteractionType<T extends Interaction>(
        @NonNull MapCodec<T>                codec,
        @NonNull StreamCodec<ByteBuf, T>    streamCodec
) { }
