package net.quepierts.thatskyinteractions.feature.client.control.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

@Getter
@Setter
@AllArgsConstructor
public final class LocalPlayerTurnEvent extends Event implements ICancellableEvent {

    private final LocalPlayer player;
    private double xo;
    private double yo;

    @Override
    public void setCanceled(final boolean canceled) {
        ICancellableEvent.super.setCanceled(canceled);
    }
}
