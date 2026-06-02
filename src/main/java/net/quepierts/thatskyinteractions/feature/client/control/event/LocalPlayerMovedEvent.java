package net.quepierts.thatskyinteractions.feature.client.control.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.joml.Vector2d;

@Getter
@RequiredArgsConstructor
public final class LocalPlayerMovedEvent extends Event implements ICancellableEvent {

    private final Player    player;
    private final Vec2      moveVector;

    @Override
    public void setCanceled(final boolean canceled) {
        ICancellableEvent.super.setCanceled(canceled);
    }
}
