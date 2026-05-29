package net.quepierts.thatskyinteractions.feature.animation;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.quepierts.thatskyinteractions.core.animation.model.ParentOverrideDefinition;
import net.quepierts.animata4j.backend.skeleton.SkeletonLayout;
import net.quepierts.animata4j.core.model.ParentOverrideConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntFunction;

@UtilityClass
public final class ParentOverrideParser {
    public static final Codec<ParentOverrideDefinition> CODEC
            = Codec.unboundedMap(Codec.STRING, Codec.STRING)
            .xmap(
                    ParentOverrideDefinition::new,
                    ParentOverrideDefinition::overrides
            );

    public static final StreamCodec<ByteBuf, ParentOverrideDefinition> STREAM_CODEC
            = ByteBufCodecs.map(
            (IntFunction<Map<String, String>>) HashMap::new,
            ByteBufCodecs.STRING_UTF8,
            ByteBufCodecs.STRING_UTF8
    ).map(
            ParentOverrideDefinition::new,
            ParentOverrideDefinition::overrides
    );

    public static ParentOverrideConfiguration parse(
            @NonNull final ParentOverrideDefinition definition,
            @NonNull final SkeletonLayout           layout
    ) {
        final var builder = ParentOverrideConfiguration.builder(layout);
        for (final var entry : definition.overrides().entrySet()) {
            builder.override(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }
}
