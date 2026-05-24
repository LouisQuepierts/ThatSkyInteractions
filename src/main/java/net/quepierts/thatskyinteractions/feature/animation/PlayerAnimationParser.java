package net.quepierts.thatskyinteractions.feature.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.experimental.UtilityClass;
import net.quepierts.thatskyinteractions.core.model.animation.PlayerAnimationDefinition;
import net.quepierts.thatskyinteractions.core.model.animation.SourceDefinition;

@UtilityClass
public class PlayerAnimationParser {

    public static final Codec<SourceDefinition> SOURCE_CODEC =
            Codec.STRING.xmap(
                    str -> new SourceDefinition(str, 0.0f, 0.0f),
                    SourceDefinition::source
            ).withAlternative(
                    RecordCodecBuilder.create(instance -> instance.group(
                            Codec.STRING.fieldOf("source").forGetter(SourceDefinition::source),
                            Codec.FLOAT.optionalFieldOf("fadeIn", 0.0f).forGetter(SourceDefinition::fadeIn),
                            Codec.FLOAT.optionalFieldOf("fadeOut", 0.0f).forGetter(SourceDefinition::fadeOut)
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

}
