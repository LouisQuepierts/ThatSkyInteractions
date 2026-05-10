package net.quepierts.thatskyinteractions.infra.animation.backend.pass;

import net.quepierts.thatskyinteractions.infra.animation.backend.pipeline.AnimationContext;
import org.jetbrains.annotations.NotNull;

public abstract class AnimationPass {

    public abstract void execute(@NotNull AnimationContext context);

}
