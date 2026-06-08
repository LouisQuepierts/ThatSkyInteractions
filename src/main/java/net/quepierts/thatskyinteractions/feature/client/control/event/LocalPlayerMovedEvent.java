package net.quepierts.thatskyinteractions.feature.client.control.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

@Getter
@RequiredArgsConstructor
public final class LocalPlayerMovedEvent extends Event implements ICancellableEvent {

    private final Player    player;
    private final Input     input;
    private final Vec2      moveVector;

    private Input           redirectInput;
    private Vec2            redirectVector;

    @Override
    public void setCanceled(final boolean canceled) {
        ICancellableEvent.super.setCanceled(canceled);
    }

    public void redirect(
            final Input input,
            final Vec2 vector
    ) {
        redirectInput = input;
        redirectVector = vector;
    }

    public boolean isRedirected() {
        return redirectInput != null && redirectVector != null;
    }
}
