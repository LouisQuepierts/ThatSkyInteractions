package net.quepierts.thatskyinteractions.feature.animation.event;

import lombok.Getter;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Avatar;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.quepierts.thatskyinteractions.feature.animation.AnimationLayerType;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationController;
import org.jspecify.annotations.NonNull;

@Getter
public abstract sealed class PlayerAnimationControllerEvent extends Event {

    private final @NonNull PlayerAnimationController        controller;
    private final @NonNull AnimationLayerType               layer;
    private final @NonNull Avatar                           entity;

    private PlayerAnimationControllerEvent(
            final @NonNull PlayerAnimationController        controller,
            final @NonNull AnimationLayerType               layer
    ) {
        this.controller                                     = controller;
        this.layer                                          = layer;
        this.entity                                         = controller.getAvatar();
    }

    @Getter
    public static abstract sealed class Play extends PlayerAnimationControllerEvent {

        private final Identifier                            animation;

        public Play(
                final @NonNull PlayerAnimationController    controller,
                final @NonNull AnimationLayerType           layer,
                final @NonNull Identifier                   animation
        ) {
            super(controller, layer);
            this.animation = animation;
        }


        @Getter
        public static final class Pre extends Play implements ICancellableEvent {

            public Pre(
                    final @NonNull PlayerAnimationController    controller,
                    final @NonNull AnimationLayerType           layer,
                    final @NonNull Identifier                   animation
            ) {
                super(controller, layer, animation);
            }

            @Override
            public void setCanceled(final boolean canceled) {
                ICancellableEvent.super.setCanceled(canceled);
            }

        }

        public static final class Post extends Play {

            public Post(
                    final @NonNull PlayerAnimationController    controller,
                    final @NonNull AnimationLayerType           layer,
                    final @NonNull Identifier                   animation
            ) {
                super(controller, layer, animation);
            }

        }
    }

    @Getter
    public static final class Finished extends PlayerAnimationControllerEvent {

        private final Identifier                            animation;

        public Finished(
                final @NonNull PlayerAnimationController    controller,
                final @NonNull AnimationLayerType           layer,
                final @NonNull Identifier                   animation
        ) {
            super(controller, layer);
            this.animation = animation;
        }

    }

    @Getter
    public static abstract sealed class State extends PlayerAnimationControllerEvent {

        private final int       lastState;
        private final int       currentState;

        public State(
                final @NonNull PlayerAnimationController    controller,
                final @NonNull AnimationLayerType           layer,
                final          int                          lastState,
                final          int                          currentState
        ) {
           super(controller, layer);

           this.lastState       = lastState;
           this.currentState    = currentState;
        }

        public static final class TransitionStart extends State {

            public TransitionStart(
                    final @NonNull PlayerAnimationController    controller,
                    final @NonNull AnimationLayerType           layer,
                    final          int                          lastState,
                    final          int                          currentState
            ) {
                super(controller, layer, lastState, currentState);
            }

        }

        public static final class TransitionEnd extends State {

            public TransitionEnd(
                    final @NonNull PlayerAnimationController    controller,
                    final @NonNull AnimationLayerType           layer,
                    final          int                          lastState,
                    final          int                          currentState
            ) {
                super(controller, layer, lastState, currentState);
            }

        }

        public static final class Loop extends State {

            public Loop(
                    final @NonNull PlayerAnimationController    controller,
                    final @NonNull AnimationLayerType           layer,
                    final          int                          state
            ) {
                super(controller, layer, state, state);
            }

        }

    }
}
