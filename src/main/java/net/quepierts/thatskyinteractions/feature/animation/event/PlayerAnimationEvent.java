package net.quepierts.thatskyinteractions.feature.animation.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationController;
import org.jspecify.annotations.NonNull;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public sealed abstract class PlayerAnimationEvent extends Event {

    private final @NonNull Player                       player;
    private final @NonNull PlayerAnimationController    controller;

    @Getter
    public static sealed abstract class Play extends PlayerAnimationEvent {

        private final Identifier animation;

        private Play(
                final @NonNull Player player,
                final @NonNull PlayerAnimationController controller,
                final @NonNull Identifier animation
        ) {
            super(player, controller);
            this.animation = animation;
        }

        public static final class Pre extends Play implements ICancellableEvent {

            public Pre(
                    final @NonNull Player player,
                    final @NonNull PlayerAnimationController controller,
                    final @NonNull Identifier animation
            ) {
                super(player, controller, animation);
            }

            @Override
            public void setCanceled(final boolean canceled) {
                ICancellableEvent.super.setCanceled(canceled);
            }
        }

        public static final class Post extends Play {

            public Post(
                    final @NonNull Player player,
                    final @NonNull PlayerAnimationController controller,
                    final @NonNull Identifier animation
            ) {
                super(player, controller, animation);
            }

        }

    }

    public static final class Abort extends PlayerAnimationEvent {

        public Abort(
                final @NonNull Player player,
                final @NonNull PlayerAnimationController controller
        ) {
            super(player, controller);
        }

    }

    public static final class Exit extends PlayerAnimationEvent {

        public Exit(
                final @NonNull Player player,
                final @NonNull PlayerAnimationController controller
        ) {
            super(player, controller);
        }

    }

    @Getter
    public static final class Signal extends PlayerAnimationEvent {

        private final String signal;

        public Signal(
                final @NonNull Player player,
                final @NonNull PlayerAnimationController controller,
                final @NonNull String signal
        ) {
            super(player, controller);
            this.signal = signal;
        }

    }

}
