package net.quepierts.thatskyinteractions.feature.animation.bedrock;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.experimental.UtilityClass;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.quepierts.thatskyinteractions.core.animation.model.bedrock.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntFunction;

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
            RecordCodecBuilder.<BedrockKeyframe>create(instance -> instance.group(
                            ExtraCodecs.VECTOR3F.fieldOf("post").forGetter(BedrockKeyframe::post),
                            ExtraCodecs.VECTOR3F.optionalFieldOf("pre").forGetter(BedrockKeyframe::pre),
                            Codec.STRING.optionalFieldOf("interpolation", BedrockKeyframe.LERP).forGetter(BedrockKeyframe::interpolation)
                    ).apply(instance, BedrockKeyframe::new)
            ).withAlternative(
                    ExtraCodecs.VECTOR3F.xmap(
                            BedrockKeyframe::of,
                            BedrockKeyframe::post
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
                    ExtraCodecs.VECTOR3F.xmap(
                            BedrockTimeline::of,
                            timeline -> timeline.keyframes().get(0f).post()
                    )
            ).withAlternative(
                    Codec.FLOAT.xmap(
                            BedrockTimeline::of,
                            timeline -> timeline.keyframes().get(0f).post().x()
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

    public static final StreamCodec<ByteBuf, BedrockKeyframe> KEYFRAME_STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VECTOR3F,
                    BedrockKeyframe::post,
                    ByteBufCodecs.VECTOR3F,
                    BedrockKeyframe::getPre,
                    ByteBufCodecs.STRING_UTF8,
                    BedrockKeyframe::interpolation,
                    (post, pre, interpolation) -> new BedrockKeyframe(
                            post,
                            Optional.of(pre),
                            interpolation
                    )
            );

    public static final StreamCodec<ByteBuf, BedrockTimeline> TIMELINE_STREAM_CODEC =
            ByteBufCodecs.map(
                    (IntFunction<Map<Float, BedrockKeyframe>>) Float2ObjectArrayMap::new,
                    ByteBufCodecs.FLOAT,
                    KEYFRAME_STREAM_CODEC
            ).map(
                    BedrockTimeline::new,
                    BedrockTimeline::keyframes
            );

    public static final StreamCodec<ByteBuf, BedrockBoneAnimation> BONE_ANIMATION_STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.optional(TIMELINE_STREAM_CODEC),
                    BedrockBoneAnimation::position,
                    ByteBufCodecs.optional(TIMELINE_STREAM_CODEC),
                    BedrockBoneAnimation::rotation,
                    ByteBufCodecs.optional(TIMELINE_STREAM_CODEC),
                    BedrockBoneAnimation::scale,
                    BedrockBoneAnimation::new
            );

    public static final StreamCodec<ByteBuf, BedrockAnimation> ANIMATION_STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL,
                    BedrockAnimation::loop,
                    ByteBufCodecs.FLOAT,
                    BedrockAnimation::length,
                    ByteBufCodecs.map(
                            Object2ObjectOpenHashMap::new,
                            ByteBufCodecs.STRING_UTF8,
                            BONE_ANIMATION_STREAM_CODEC
                    ),
                    BedrockAnimation::bones,
                    BedrockAnimation::new
            );

    public static final StreamCodec<ByteBuf, BedrockAnimationDefinition> ANIMATION_DEFINITION_STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    BedrockAnimationDefinition::formatVersion,
                    ByteBufCodecs.map(
                            HashMap::new,
                            ByteBufCodecs.STRING_UTF8,
                            ANIMATION_STREAM_CODEC
                    ),
                    BedrockAnimationDefinition::animations,
                    ByteBufCodecs.optional(
                            ByteBufCodecs.map(
                                    Object2ObjectOpenHashMap::new,
                                    ByteBufCodecs.STRING_UTF8,
                                    ByteBufCodecs.STRING_UTF8
                            )
                    ),
                    BedrockAnimationDefinition::metadata,
                    BedrockAnimationDefinition::new
            );

}
