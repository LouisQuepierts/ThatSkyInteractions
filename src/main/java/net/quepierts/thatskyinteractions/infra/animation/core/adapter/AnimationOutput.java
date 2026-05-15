package net.quepierts.thatskyinteractions.infra.animation.core.adapter;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.infra.animation.backend.pipeline.AnimationResultView;
import org.jspecify.annotations.NonNull;

public interface AnimationOutput {

    static AnimationOutput compose(@NonNull AnimationOutput... outputs) {
        return compose(outputs);
    }

    void accept(@NonNull AnimationResultView buffer);

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
     class Composed implements AnimationOutput {
        private final @lombok.NonNull AnimationOutput[] outputs;

        @Override
        public void accept(@NonNull final AnimationResultView buffer) {
            for (final var output : outputs) {
                output.accept(buffer);
            }
        }
    }

}
