package net.quepierts.thatskyinteractions.infra.animation.adapter;

import net.quepierts.thatskyinteractions.infra.animation.backend.pipeline.AnimationResultView;
import org.jetbrains.annotations.NotNull;

public interface AnimationOutput {

    void accept(@NotNull AnimationResultView buffer);

}
