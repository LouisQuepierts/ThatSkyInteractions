package net.quepierts.thatskyinteractions.infra.animation.tween.backend.task;

public interface Task {

    /**
     * @param delta time delta
     * @return true if task is finished
     */
    boolean update(float delta);
}
