package net.quepierts.thatskyinteractions.infra.animation.core.adapter;

import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.WritableBuffer;

public interface PipelineInputProvider {

    void fill(int channel, int offset, WritableBuffer out);

}
