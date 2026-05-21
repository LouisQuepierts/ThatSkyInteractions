package net.quepierts.thatskyinteractions.infra.animation.tween.backend.task;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class TweenTask {

    private final float duration;

}
