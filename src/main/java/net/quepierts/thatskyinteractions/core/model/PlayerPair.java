package net.quepierts.thatskyinteractions.core.model;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
public class PlayerPair {

    @NotNull private final UUID left;
    @NotNull private final UUID right;

    public PlayerPair(@NotNull UUID a, @NotNull UUID b) {
        if (a.compareTo(b) > 0) {
            this.left = a;
            this.right = b;
        } else {
            this.left = b;
            this.right = a;
        }
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
