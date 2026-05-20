package net.quepierts.thatskyinteractions.infra.animation.tween.backend;

import org.jspecify.annotations.NonNull;

public interface TweenTickRegistrar {

    void register(@NonNull final TweenTickHandler handler);

}
