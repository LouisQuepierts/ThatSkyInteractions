package net.quepierts.thatskyinteractions.infra.animation.backend.pass.definition;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.infra.animation.backend.pass.AnimationPass;
import net.quepierts.thatskyinteractions.infra.animation.backend.pipeline.PipelineCompileContext;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public abstract class AnimationPassDefinition {

    private final String name;

    public static ComputePassDefinition compute(String name) {
        return new ComputePassDefinition(name);
    }

    public abstract AnimationPass compile(@NotNull PipelineCompileContext context);

    public enum Type {
        PARAMETER,
        COMPUTE,
        POSTPROCESS
    }
}
