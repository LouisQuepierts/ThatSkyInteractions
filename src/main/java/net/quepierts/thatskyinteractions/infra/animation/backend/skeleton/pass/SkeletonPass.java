package net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pass;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline.SkeletonContext;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class SkeletonPass {

    private final String name;

    public abstract void execute(@NonNull SkeletonContext context);

}
