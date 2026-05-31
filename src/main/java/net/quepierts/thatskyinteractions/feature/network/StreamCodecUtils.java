package net.quepierts.thatskyinteractions.feature.network;

import io.netty.buffer.ByteBuf;
import lombok.experimental.UtilityClass;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

@UtilityClass
public class StreamCodecUtils {

    public static <T> StreamCodec<ByteBuf, T> unit(@NonNull final Supplier<T> supplier) {
        return new StreamCodec<>() {
            @Override
            public T decode(final ByteBuf byteBuf) {
                return supplier.get();
            }

            @Override
            public void encode(final ByteBuf o, final T t) {

            }
        };
    }

}
