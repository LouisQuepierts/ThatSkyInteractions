package net.quepierts.thatskyinteractions.feature.interaction;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.resources.Identifier;

import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class InteractionRequest {

    private final UUID other;
    private final Identifier type;
    private final long expireTime;

    private State state = State.WAITING;

    public InteractionRequest(
            UUID other,
            Identifier type,
            long expireTime
    ) {
        this.other = other;
        this.type = type;
        this.expireTime = expireTime;
    }

    public static InteractionRequest send(
            UUID other,
            Identifier type,
            long tick
    ) {
        return new InteractionRequest(other, type, tick + 20 * 60, State.WAITING);
    }

    public static InteractionRequest receive(
            UUID other,
            Identifier type,
            long expireTime
    ) {
        return new InteractionRequest(other, type, expireTime, State.WAITING);
    }

    public boolean isExpired(long tick) {
        return tick >= this.expireTime;
    }

    public void cancel() {
        this.state = State.CANCELED;
    }

    public void accept() {
        this.state = State.DONE;
    }

    public boolean isWaiting() {
        return this.state == State.WAITING;
    }

    public boolean isCanceled() {
        return this.state == State.CANCELED;
    }

    public boolean isDone() {
        return this.state == State.DONE;
    }

    public enum State {
        WAITING,
        DONE,
        CANCELED,
    }

}
