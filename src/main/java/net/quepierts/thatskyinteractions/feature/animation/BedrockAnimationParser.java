package net.quepierts.thatskyinteractions.feature.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.experimental.UtilityClass;
import net.minecraft.util.ExtraCodecs;
import net.quepierts.thatskyinteractions.core.model.animation.*;

import java.util.Optional;

@UtilityClass
public class BedrockAnimationParser {

    /**
     * {@snippet lang = JSON :
     * {
     *     "1.0": [ 0.0, 0.0, 0.0],
     *     "2.0": {
     *         "pre": [ 0.0, 0.0, 0.0],
     *         "post": [1.0, 1.0, 1.0],
     *         "lerp_mode": "linear"
     *     }
     * }
     * }
     * */
    public static final Codec<BedrockKeyframe> KEYFRAME_CODEC =
            ExtraCodecs.VECTOR3F.xmap(
                    BedrockKeyframe::of,
                    BedrockKeyframe::post
            ).withAlternative(
                    RecordCodecBuilder.create(instance -> instance.group(
                                    ExtraCodecs.VECTOR3F.fieldOf("post").forGetter(BedrockKeyframe::post),
                                    ExtraCodecs.VECTOR3F.optionalFieldOf("pre").forGetter(BedrockKeyframe::pre),
                                    Codec.STRING.optionalFieldOf("interpolation", BedrockKeyframe.LERP).forGetter(BedrockKeyframe::interpolation)
                            ).apply(instance, BedrockKeyframe::new)
                    )
            );

    public static final Codec<BedrockTimeline> TIMELINE_CODEC =
            Codec.unboundedMap(
                    Codec.STRING.xmap(
                            Float::parseFloat,
                            f -> Float.toString(f)
                    ),
                    KEYFRAME_CODEC
            ).xmap(
                    BedrockTimeline::new,
                    BedrockTimeline::keyframes
            ).withAlternative(
                    Codec.FLOAT.xmap(
                            BedrockTimeline::of,
                            timeline -> timeline.keyframes().get(0f).post().x()
                    )
            ).withAlternative(
                    ExtraCodecs.VECTOR3F.xmap(
                            BedrockTimeline::of,
                            timeline -> timeline.keyframes().get(0f).post()
                    )
            );

    public static final Codec<BedrockBoneAnimation> BONE_ANIMATION_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    TIMELINE_CODEC.optionalFieldOf("position").forGetter(BedrockBoneAnimation::position),
                    TIMELINE_CODEC.optionalFieldOf("rotation").forGetter(BedrockBoneAnimation::rotation),
                    TIMELINE_CODEC.optionalFieldOf("scale").forGetter(BedrockBoneAnimation::scale)
            ).apply(instance, BedrockBoneAnimation::new));

    public static final Codec<BedrockAnimation> ANIMATION_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.BOOL.optionalFieldOf("loop", false).forGetter(BedrockAnimation::loop),
                    Codec.FLOAT.fieldOf("animation_length").forGetter(BedrockAnimation::length),
                    Codec.unboundedMap(
                            Codec.STRING,
                            BONE_ANIMATION_CODEC
                    ).fieldOf("bones").forGetter(BedrockAnimation::bones)
            ).apply(instance, BedrockAnimation::new));

    public static final Codec<BedrockAnimationDefinition> ANIMATION_DEFINITION_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("format_version").forGetter(BedrockAnimationDefinition::formatVersion),
                    Codec.unboundedMap(
                            Codec.STRING,
                            ANIMATION_CODEC
                    ).fieldOf("animations").forGetter(BedrockAnimationDefinition::animations),
                    Codec.unboundedMap(
                            Codec.STRING,
                            Codec.STRING
                    ).optionalFieldOf("metadata").forGetter(BedrockAnimationDefinition::metadata)
            ).apply(instance, BedrockAnimationDefinition::new));

}
