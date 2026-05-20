package net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pass.definition;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pass.SkeletonPass;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline.SkeletonPipelineCompileContext;
import org.jspecify.annotations.NonNull;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class SkeletonPassDefinition {

    private final String name;

    public abstract SkeletonPass compile(@NonNull SkeletonPipelineCompileContext context);

}
