package net.quepierts.thatskyinteractions.infra.animation.core.adapter;

import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline.PoseView;

public interface TransformProvider {
    
    void write(final PoseView target);
    
}
