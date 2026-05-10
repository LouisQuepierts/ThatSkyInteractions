package net.quepierts.thatskyinteractions.infra.animation.backend.pipeline;

import lombok.experimental.UtilityClass;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.DefaultChannelFormats;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.DefaultChannelLayouts;
import net.quepierts.thatskyinteractions.infra.animation.backend.pass.definition.AnimationPassDefinition;

@UtilityClass
public class DefaultAnimationPipelines {

    public static final AnimationPipeline IMMEDIATE = AnimationPipeline.compiler()
            .withChannelLayout(DefaultChannelLayouts.HUMANOID)
            .withChannelFormat(DefaultChannelFormats.TIMELINE)
            .withSampler("Pipeline.OriginSampler")
            .withSampler("SourceSampler")

            .withPass(
                    AnimationPassDefinition.compute("ComputePass")
                            .sample("SourceSampler", "Pipeline.ResultBuffer")
            )

            .compile();

}
