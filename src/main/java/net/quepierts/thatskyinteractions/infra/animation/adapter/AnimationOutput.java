package net.quepierts.thatskyinteractions.infra.animation.adapter;

import net.quepierts.thatskyinteractions.infra.animation.backend.pipeline.AnimationResultView;
import org.jspecify.annotations.NonNull;

public interface AnimationOutput {

    void accept(@NonNull AnimationResultView buffer);

}
