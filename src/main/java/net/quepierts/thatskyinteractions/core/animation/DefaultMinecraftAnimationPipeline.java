package net.quepierts.thatskyinteractions.core.animation;

import lombok.experimental.UtilityClass;
import net.quepierts.veynir.backend.channel.DefaultChannelFormats;
import net.quepierts.veynir.backend.pass.definition.AnimationPassDefinition;
import net.quepierts.veynir.backend.pipeline.AnimationPipeline;
import net.quepierts.veynir.backend.uniform.UniformType;

@UtilityClass
public class DefaultMinecraftAnimationPipeline {

    public static final AnimationPipeline HUMANOID_TIMELINE = AnimationPipeline.compiler()
            .withChannelLayout(DefaultMinecraftChannelLayout.HUMANOID)
            .withChannelFormat(DefaultMinecraftChannelFormat.HUMANOID)
            .withSampler(AnimationPipeline.ORIGINAL_SAMPLER)
            .withSampler("TimelineSampler")
            .withPass(
                    AnimationPassDefinition.compute("ComputePass")
                            .sample("TimelineSampler", AnimationPipeline.OUTPUT_BUFFER)
            )
            .compile();

    public static final AnimationPipeline HUMANOID_BLEND = AnimationPipeline.compiler()
            .withChannelLayout(DefaultMinecraftChannelLayout.HUMANOID)
            .withChannelFormat(DefaultMinecraftChannelFormat.HUMANOID)

            .withBuffer("Buffer#0")
            .withBuffer("Buffer#1")

            .withSampler("Sampler#0")
            .withSampler("Sampler#1")

            .withUniform("uWeight", UniformType.FLOAT)

            .withPass(
                    AnimationPassDefinition.compute("ComputePass")
                            .sample("Sampler#0", "Buffer#0")
                            .semantic("Compute.Sample#0")
                            .sample("Sampler#1", "Buffer#1")
                            .semantic("Compute.Sample#1")
                            .blend(
                                    "Buffer#0",
                                    "Buffer#1",
                                    AnimationPipeline.OUTPUT_BUFFER,
                                    "uWeight"
                            )
            )
            .compile();

}
