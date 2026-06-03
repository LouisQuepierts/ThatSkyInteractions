package net.quepierts.thatskyinteractions.feature.animation.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationController;
import org.jspecify.annotations.NonNull;

@Getter
@RequiredArgsConstructor
public abstract sealed class PlayerAnimationControllerEvent extends Event {

    private final @NonNull PlayerAnimationController controller;

    @Getter
    public static abstract sealed class Play extends PlayerAnimationControllerEvent {

        private final Identifier animation;

        public Play(
                final @NonNull PlayerAnimationController    controller,
                final @NonNull Identifier                   animation
        ) {
            super(controller);
            this.animation = animation;
        }


        @Getter
        public static final class Pre extends Play implements ICancellableEvent {

            public Pre(
                    final @NonNull PlayerAnimationController    controller,
                    final @NonNull Identifier                   animation
            ) {
                super(controller, animation);
            }

            @Override
            public void setCanceled(final boolean canceled) {
                ICancellableEvent.super.setCanceled(canceled);
            }

        }

        public static final class Post extends Play {

            public Post(
                    final @NonNull PlayerAnimationController    controller,
                    final @NonNull Identifier                   animation
            ) {
                super(controller, animation);
            }

        }
    }

    public static final class Finished extends PlayerAnimationControllerEvent {

        public Finished(
                @NonNull final PlayerAnimationController controller
        ) {
            super(controller);
        }

    }

    @Getter
    public static final class StateChanged extends PlayerAnimationControllerEvent {

        private final int       lastState;
        private final int       currentState;

        private final boolean   looping;

        public StateChanged(
                @NonNull final PlayerAnimationController controller,
                final int lastState,
                final int currentState
        ) {
           super(controller);

           this.lastState       = lastState;
           this.currentState    = currentState;
           this.looping         = lastState == currentState;
        }
    }
}
