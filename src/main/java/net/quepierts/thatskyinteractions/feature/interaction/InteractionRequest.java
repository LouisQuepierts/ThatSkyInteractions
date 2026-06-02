package net.quepierts.thatskyinteractions.feature.interaction;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.UUID;

public final class InteractionRequest {

    private final UUID other;
    private final Identifier type;
    private final long expireTime;

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
        return new InteractionRequest(other, type, tick + 20 * 60);
    }

    public static InteractionRequest receive(
            UUID other,
            Identifier type,
            long expireTime
    ) {
        return new InteractionRequest(other, type, expireTime);
    }

    public boolean isExpired(long tick) {
        return tick >= this.expireTime;
    }

    public UUID other() {
        return other;
    }

    public Identifier type() {
        return type;
    }

    public long expireTime() {
        return expireTime;
    }

    public enum State {
        WAITING,
        ONGOING
    }

}
