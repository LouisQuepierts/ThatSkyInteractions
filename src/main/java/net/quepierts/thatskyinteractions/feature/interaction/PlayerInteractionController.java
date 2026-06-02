package net.quepierts.thatskyinteractions.feature.interaction;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.NonNull;

import java.util.*;

public final class PlayerInteractionController {

    private final Map<UUID, InteractionRequest> received;
    private InteractionRequest                  sent;
    private InteractionRequest                  handling;

    public PlayerInteractionController() {
        this.received = new HashMap<>();
    }

    public void sendInvite(
            final @NonNull Player       other,
            final @NonNull Identifier   type
    ) {

        this.sent = InteractionRequest.send(
                other.getUUID(),
                type,
                other.level().getGameTime()
        );

    }

    public void receiveInvite(
            final @NonNull Player       other,
            final @NonNull Identifier   type
    ) {

        this.received.put(
                other.getUUID(),
                InteractionRequest.receive(
                        other.getUUID(),
                        type,
                        other.level().getGameTime()
                )
        );

    }

    public void cancelSent() {

        this.sent = null;

    }

    public void cancelReceived(
            final @NonNull Player       other
    ) {

        this.received.remove(other.getUUID());

    }

    public void sendAccept(
            final @NonNull Player       other
    ) {

    }

    public void receiveAccept(
            final @NonNull Player       other
    ) {


    }

    public void tick() {

    }

    public boolean hasSentRequest() {
        return this.sent != null;
    }

    public Collection<InteractionRequest> getReceivedRequests() {
        return this.received.values();
    }

}
