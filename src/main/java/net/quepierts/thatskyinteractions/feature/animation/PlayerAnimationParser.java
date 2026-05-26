package net.quepierts.thatskyinteractions.feature.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.experimental.UtilityClass;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerAnimationDefinition;
import net.quepierts.thatskyinteractions.core.animation.model.SourceDefinition;

@UtilityClass
public class PlayerAnimationParser {

    public static final float DEFAULT_TRANSITION = 0.0f;

    public static final Codec<SourceDefinition> SOURCE_CODEC =
            Codec.STRING.xmap(
                    str -> new SourceDefinition(str, DEFAULT_TRANSITION, DEFAULT_TRANSITION),
                    SourceDefinition::source
            ).withAlternative(
                    RecordCodecBuilder.create(instance -> instance.group(
                            Codec.STRING.fieldOf("source").forGetter(SourceDefinition::source),
                            Codec.FLOAT.optionalFieldOf("fadeIn", DEFAULT_TRANSITION).forGetter(SourceDefinition::fadeIn),
                            Codec.FLOAT.optionalFieldOf("fadeOut", DEFAULT_TRANSITION).forGetter(SourceDefinition::fadeOut)
                    ).apply(instance, SourceDefinition::new))
            );

    public static final Codec<PlayerAnimationDefinition> ANIMATION_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.optionalFieldOf("type", "simple").forGetter(PlayerAnimationDefinition::type),
                    Codec.unboundedMap(
                            Codec.STRING,
                            SOURCE_CODEC
                    ).fieldOf("sources").forGetter(PlayerAnimationDefinition::sources)
            ).apply(instance, PlayerAnimationDefinition::new));

    public static final StreamCodec<ByteBuf, SourceDefinition> SOURCE_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SourceDefinition::source,
            ByteBufCodecs.FLOAT,
            SourceDefinition::fadeIn,
            ByteBufCodecs.FLOAT,
            SourceDefinition::fadeOut,
            SourceDefinition::new
    );

    public static final StreamCodec<ByteBuf, PlayerAnimationDefinition> ANIMATION_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            PlayerAnimationDefinition::type,
            ByteBufCodecs.map(
                    Object2ObjectOpenHashMap::new,
                    ByteBufCodecs.STRING_UTF8,
                    SOURCE_STREAM_CODEC
            ),
            PlayerAnimationDefinition::sources,
            PlayerAnimationDefinition::new
    );

}
