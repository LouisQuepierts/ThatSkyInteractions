package net.quepierts.thatskyinteractions.feature.interaction.event;

import lombok.Getter;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jspecify.annotations.NonNull;

@Getter
public abstract sealed class PlayerInteractionEvent extends Event {

    private final Player    requester;
    private final Player    receiver;
    private final boolean   isClient;

    protected PlayerInteractionEvent(
            final @NonNull Player requester,
            final @NonNull Player receiver
    ) {
        this.requester      = requester;
        this.receiver       = receiver;
        this.isClient       = requester.level().isClientSide();
    }

    @Getter
    public static abstract sealed class Invite extends PlayerInteractionEvent {

        private final Identifier    interaction;

        protected Invite(
                final @NonNull Player       requester,
                final @NonNull Player       receiver,
                final @NonNull Identifier   interaction
        ) {
            super(requester, receiver);
            this.interaction    = interaction;
        }

        public static final class Pre extends Invite implements ICancellableEvent {

            public Pre(
                    final @NonNull Player       requester,
                    final @NonNull Player       receiver,
                    final @NonNull Identifier   interaction
            ) {
                super(requester, receiver, interaction);
            }

            @Override
            public void setCanceled(final boolean canceled) {
                ICancellableEvent.super.setCanceled(canceled);
            }

        }

        public static final class Post extends Invite {

            public Post(
                    final @NonNull Player       requester,
                    final @NonNull Player       receiver,
                    final @NonNull Identifier   interaction
            ) {
                super(requester, receiver, interaction);
            }

        }

    }

    public static abstract sealed class Accept extends PlayerInteractionEvent {

        public Accept(
                final @NonNull Player       requester,
                final @NonNull Player       receiver
        ) {
            super(requester, receiver);
        }

        public static final class Pre extends Accept implements ICancellableEvent {

            public Pre(
                    final @NonNull Player       requester,
                    final @NonNull Player       receiver
            ) {
                super(requester, receiver);
            }

            @Override
            public void setCanceled(final boolean canceled) {
                ICancellableEvent.super.setCanceled(canceled);
            }

        }

        public static final class Post extends Accept {

            public Post(
                    final @NonNull Player       requester,
                    final @NonNull Player       receiver
            ) {
                super(requester, receiver);
            }

        }

    }

    public static final class Cancel extends PlayerInteractionEvent {

        public Cancel(
                final @NonNull Player       requester,
                final @NonNull Player       receiver
        ) {
            super(requester, receiver);
        }

    }

}
