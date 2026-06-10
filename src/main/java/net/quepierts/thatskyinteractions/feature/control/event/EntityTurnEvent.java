package net.quepierts.thatskyinteractions.feature.control.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jspecify.annotations.NonNull;

@Getter
@RequiredArgsConstructor
public abstract sealed class EntityTurnEvent extends Event {

    private final @NonNull Entity entity;
    private final double xo;
    private final double yo;

    public static final class Pre extends EntityTurnEvent implements ICancellableEvent {
        public Pre(@NonNull Entity entity, double xo, double yo) {
            super(entity, xo, yo);
        }

        @Override
        public void setCanceled(final boolean canceled) {
            ICancellableEvent.super.setCanceled(canceled);
        }
    }

    public static final class Post extends EntityTurnEvent {
        public Post(@NonNull Entity entity, double xo, double yo) {
            super(entity, xo, yo);
        }
    }

}
