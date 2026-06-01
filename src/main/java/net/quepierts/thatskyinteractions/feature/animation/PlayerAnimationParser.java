package net.quepierts.thatskyinteractions.feature.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.experimental.UtilityClass;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerAnimationDefinition;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerMask;
import net.quepierts.thatskyinteractions.core.animation.model.SourceDefinition;

@UtilityClass
public class PlayerAnimationParser {

    public static final float DEFAULT_TRANSITION = 0.0f;

    public static final Codec<SourceDefinition> SOURCE_CODEC =
            RecordCodecBuilder.<SourceDefinition>create(instance -> instance.group(
                    Codec.STRING.fieldOf("source").forGetter(SourceDefinition::source),
                    Codec.FLOAT.optionalFieldOf("fadeIn", DEFAULT_TRANSITION).forGetter(SourceDefinition::fadeIn),
                    Codec.FLOAT.optionalFieldOf("fadeOut", DEFAULT_TRANSITION).forGetter(SourceDefinition::fadeOut),
                    Codec.STRING.optionalFieldOf("namespace").forGetter(SourceDefinition::namespace)
            ).apply(instance, SourceDefinition::new)).withAlternative(
                    Codec.STRING.xmap(
                            SourceDefinition::of,
                            SourceDefinition::source
                    )
            );

    public static final Codec<PlayerAnimationDefinition> ANIMATION_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.optionalFieldOf("type", "simple").forGetter(PlayerAnimationDefinition::type),
                    Codec.STRING.optionalFieldOf("override", "thatskyinteractions:disabled").forGetter(PlayerAnimationDefinition::override),
                    Codec.unboundedMap(
                            Codec.STRING,
                            SOURCE_CODEC
                    ).fieldOf("sources").forGetter(PlayerAnimationDefinition::sources),
                    PlayerMaskParser.CODEC.optionalFieldOf("unlock", PlayerMask.direct(0)).forGetter(PlayerAnimationDefinition::unlock),
                    Codec.BOOL.optionalFieldOf("abortable", false).forGetter(PlayerAnimationDefinition::abortable),
                    Codec.BOOL.optionalFieldOf("restrictMotion", true).forGetter(PlayerAnimationDefinition::restrictMotion),
                    Codec.BOOL.optionalFieldOf("rootMotion", false).forGetter(PlayerAnimationDefinition::rootMotion)
            ).apply(instance, PlayerAnimationDefinition::new));

    public static final StreamCodec<ByteBuf, SourceDefinition> SOURCE_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SourceDefinition::source,
            ByteBufCodecs.FLOAT,
            SourceDefinition::fadeIn,
            ByteBufCodecs.FLOAT,
            SourceDefinition::fadeOut,
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8),
            SourceDefinition::namespace,
            SourceDefinition::new
    );

    public static final StreamCodec<ByteBuf, PlayerAnimationDefinition> ANIMATION_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            PlayerAnimationDefinition::type,
            ByteBufCodecs.STRING_UTF8,
            PlayerAnimationDefinition::override,
            ByteBufCodecs.map(
                    Object2ObjectOpenHashMap::new,
                    ByteBufCodecs.STRING_UTF8,
                    SOURCE_STREAM_CODEC
            ),
            PlayerAnimationDefinition::sources,
            PlayerMaskParser.STREAM_CODEC,
            PlayerAnimationDefinition::unlock,
            ByteBufCodecs.BOOL,
            PlayerAnimationDefinition::abortable,
            ByteBufCodecs.BOOL,
            PlayerAnimationDefinition::restrictMotion,
            ByteBufCodecs.BOOL,
            PlayerAnimationDefinition::rootMotion,
            PlayerAnimationDefinition::new
    );

}
