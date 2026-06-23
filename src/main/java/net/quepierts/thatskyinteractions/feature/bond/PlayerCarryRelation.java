package net.quepierts.thatskyinteractions.feature.bond;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.ref.WeakReference;

public class PlayerCarryRelation {

    private static final WeakReference<Player> NULL = new WeakReference<>(null);

    private @NonNull WeakReference<Player> carried = NULL;
    private @NonNull WeakReference<Player> carrier = NULL;

    public void carry(
            final @NonNull Player   carried
    ) {
        this.carried = new WeakReference<>(carried);
    }

    public void ride(
            final @NonNull Player   carrier
    ) {
        this.carrier = new WeakReference<>(carrier);
    }

    public boolean canCarry(
            final @NonNull Player   player
    ) {
        return this.carried.get() == null && this.carrier.get() != player;
    }

    public boolean canRide(
            final @NonNull Player   player
    ) {
        return this.carrier.get() == null && this.carried.get() != player;
    }

    public void unCarry() {
        this.carried = NULL;
    }

    public void unRide() {
        this.carrier = NULL;
    }

    public boolean isCarried(
            final @NonNull Player   player
    ) {
        return this.carried.get() == player;
    }

    public boolean isCarrier(
            final @NonNull Player   player
    ) {
        return this.carrier.get() == player;
    }

    public boolean isCarrying() {
        return this.carried.get() != null;
    }

    public boolean isBeingCarried() {
        return this.carrier.get() != null;
    }

    public @Nullable Player getCarried() {
        return this.carried.get();
    }

    public @Nullable Player getCarrier() {
        return this.carrier.get();
    }

}
