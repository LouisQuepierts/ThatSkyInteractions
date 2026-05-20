package net.quepierts.thatskyinteractions.feature.animation;

import lombok.experimental.UtilityClass;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pass.definition.PivotPassDefinition;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline.SkeletonPipeline;

@UtilityClass
public class DefaultMinecraftSkeletonPipeline {

    public static final SkeletonPipeline HUMANOID = SkeletonPipeline.compiler()
            .withLayout(DefaultMinecraftSkeletonLayout.HUMANOID)
            .withUniform(PivotPassDefinition.PIVOTS_UBO_NAME)
            .withPass(new PivotPassDefinition("PivotPass")
                    .src(SkeletonPipeline.INPUT_BUFFER)
                    .dst(SkeletonPipeline.OUTPUT_BUFFER)
            )
            .compile();

}
