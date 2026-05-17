package net.quepierts.thatskyinteractions.infra.animation.backend.pass.definition;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.infra.animation.backend.pass.AnimationPass;
import net.quepierts.thatskyinteractions.infra.animation.backend.pipeline.AnimationPipelineCompileContext;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public abstract class AnimationPassDefinition {

    private final String    name;
    private final PassType  type;

    public static OperationComputePassDefinition compute(String name) {
        return new OperationComputePassDefinition(name);
    }

    public abstract AnimationPass compile(@NotNull AnimationPipelineCompileContext context);

}
