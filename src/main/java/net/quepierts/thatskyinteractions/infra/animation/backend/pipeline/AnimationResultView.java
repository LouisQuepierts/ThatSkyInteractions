package net.quepierts.thatskyinteractions.infra.animation.backend.pipeline;

import net.quepierts.thatskyinteractions.infra.animation.adapter.Consumer4f;

public interface AnimationResultView {

    void read(int channel, Consumer4f consumer);

    void read(int channel, float[] out);

}
