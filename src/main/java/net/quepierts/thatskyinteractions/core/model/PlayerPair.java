package net.quepierts.thatskyinteractions.core.model;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
public class PlayerPair {

    @NotNull private final UUID left;
    @NotNull private final UUID right;

    public static PlayerPair of(@NotNull UUID a, @NotNull UUID b) {
        return new PlayerPair(a, b);
    }

    public PlayerPair(@NotNull UUID a, @NotNull UUID b) {
        if (a.equals(b)) {
            throw new IllegalArgumentException("Cannot create pair with same uuid!");
        }

        if (a.compareTo(b) > 0) {
            this.left = a;
            this.right = b;
        } else {
            this.left = b;
            this.right = a;
        }
    }

    public UUID getOther(UUID uuid) {
        return uuid.equals(this.left) ? this.right : this.left;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PlayerPair other) {
            return this.left.equals(other.left) && this.right.equals(other.right);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return left.hashCode() ^ right.hashCode();
    }
}
