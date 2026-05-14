package net.quepierts.thatskyinteractions.feature.animation;

import lombok.experimental.UtilityClass;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.DefaultChannelFormats;
import net.quepierts.thatskyinteractions.infra.animation.backend.pass.definition.AnimationPassDefinition;
import net.quepierts.thatskyinteractions.infra.animation.backend.pipeline.AnimationPipeline;

@UtilityClass
public class DefaultMinecraftAnimationPipeline {

    public static final AnimationPipeline HUMANOID_TIMELINE = AnimationPipeline.compiler()
            .withChannelLayout(DefaultMinecraftChannelLayout.HUMANOID)
            .withChannelFormat(DefaultChannelFormats.TIMELINE)
            .withSampler(AnimationPipeline.ORIGINAL_SAMPLER)
            .withSampler("TimelineSampler")
            .withPass(
                    AnimationPassDefinition.compute("ComputePass")
                            .sample("TimelineSampler", AnimationPipeline.OUTPUT_BUFFER)
            )
            .compile();

}
