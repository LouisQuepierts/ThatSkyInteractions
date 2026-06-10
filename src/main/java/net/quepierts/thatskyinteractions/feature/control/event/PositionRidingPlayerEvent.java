package net.quepierts.thatskyinteractions.feature.control.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public class PositionRidingPlayerEvent extends Event implements ICancellableEvent {

    private final @NotNull Player player;
    private final @NotNull Player passenger;

    @Override
    public void setCanceled(final boolean canceled) {
        ICancellableEvent.super.setCanceled(canceled);
    }
}
