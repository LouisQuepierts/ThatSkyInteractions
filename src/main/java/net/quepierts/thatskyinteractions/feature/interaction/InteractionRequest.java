package net.quepierts.thatskyinteractions.feature.interaction;

import lombok.*;
import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.feature.expression.InteractionRequesterExpression;

import java.lang.ref.WeakReference;
import java.util.UUID;

@Getter
public final class InteractionRequest {

    private final   UUID                        other;
    private final   Identifier                  type;
    private final   WeakReference<Interaction>  interaction;
    private final   long                        expireTime;
    private final   boolean                     requester;

    private State   state   = State.WAITING;
    private int     timer   = 0;

    public static InteractionRequest send(
            UUID other,
            Identifier type,
            long tick
    ) {
        return new InteractionRequest(other, type, tick + 20 * 60, true);
    }

    public static InteractionRequest receive(
            UUID other,
            Identifier type,
            long tick
    ) {
        return new InteractionRequest(other, type, tick + 20 * 60, false);
    }

    private InteractionRequest(
            final @NonNull UUID         other,
            final @NonNull Identifier   type,
            final          long         expireTime,
            final          boolean      requester
    ) {

        this.other                      = other;
        this.type                       = type;
        this.expireTime                 = expireTime;
        this.requester                  = requester;

        final var manager               = PlayerInteractionManager.getInstance();
        final var interaction           = manager.get(type);

        this.interaction                = new WeakReference<>(interaction);

    }

    public boolean isExpired(long tick) {
        return tick >= this.expireTime;
    }

    public void cancel() {
        this.state = State.CANCELED;
    }

    public void accept() {
        this.state = State.RUNNING;
    }

    public void done() {
        this.state = State.DONE;
    }

    public boolean isWaiting() {
        return this.state == State.WAITING;
    }

    public boolean isRunning() {
        return this.state == State.RUNNING;
    }

    public boolean isCanceled() {
        return this.state == State.CANCELED;
    }

    public boolean isDone() {
        return this.state == State.DONE;
    }

    public enum State {
        WAITING,
        RUNNING,
        DONE,
        CANCELED,
    }

}
