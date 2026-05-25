package net.quepierts.thatskyinteractions.core.animation;

import lombok.experimental.UtilityClass;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pass.definition.FetchPassDefinition;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pass.definition.MergePassDefinition;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pass.definition.ParentOverridePassDefinition;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pass.definition.PivotPassDefinition;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline.SkeletonPipeline;

@UtilityClass
public class DefaultMinecraftSkeletonPipeline {
    
    private static final String TEMP_BUFFER = "TempBuffer";

    public static final SkeletonPipeline MODIFIED_HUMANOID = SkeletonPipeline.compiler()
            .withLayout(DefaultMinecraftSkeletonLayout.HUMANOID)

            .withProvider("SourceProvider")
            .withBuffer(TEMP_BUFFER)

            .withUniform(PivotPassDefinition.REQUIRED_UBO)
            .withUniform(ParentOverridePassDefinition.REQUIRED_UBO)
            .withUniform("OverrideMask")

            .withPass(new FetchPassDefinition("FetchPass")
                    .src("SourceProvider")
                    .dst(TEMP_BUFFER)
            )
            .withPass(new MergePassDefinition("MergePass")
                    .src0(SkeletonPipeline.INPUT_BUFFER)
                    .src1(TEMP_BUFFER)
                    .dst(SkeletonPipeline.OUTPUT_BUFFER)
            )
            .withPass(new PivotPassDefinition("PivotPass")
                    .src(SkeletonPipeline.OUTPUT_BUFFER)
                    .dst(SkeletonPipeline.OUTPUT_BUFFER)
            )
            .withPass(new ParentOverridePassDefinition("ParentOverridePass")
                    .src1(SkeletonPipeline.OUTPUT_BUFFER)
                    .dst(SkeletonPipeline.OUTPUT_BUFFER)
            )
            .compile();

}
