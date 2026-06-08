package net.quepierts.thatskyinteractions.infra.animation.tween.backend.task;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public final class TweenScheduler {

    private final List<Task> tasks = new ArrayList<>();

    public void submit(@NonNull final Task task) {
        this.tasks.add(task);
    }

    public void update(final float delta) {
        final var iterator = tasks.iterator();

        while (iterator.hasNext()) {
            final var task = iterator.next();
            if (task.update(delta)) {
                iterator.remove();
            }
        }

    }

    public boolean isRunning() {
        return !this.tasks.isEmpty();
    }

    public void clear() {
        this.tasks.clear();
    }
}
