package net.quepierts.thatskyinteractions.feature.expression.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jspecify.annotations.NonNull;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public sealed abstract class PlayerExpressionEvent extends Event {

    private final Player        player;
    private final Identifier    expression;

    @Getter
    public static sealed abstract class Enqueue extends PlayerExpressionEvent {

        private final int       level;

        private Enqueue(
                final @NonNull  Player      player, 
                final @NonNull  Identifier  expression,
                final           int         level
        ) {
            super(player, expression);
            this.level = level;
        }
        
        public static final class Pre extends Enqueue implements ICancellableEvent {
            public Pre(
                final @NonNull  Player      player, 
                final @NonNull  Identifier  expression,
                final           int         level
            ) {
                super(player, expression, level);
            }
        }
        
        public static final class Post extends Enqueue {
            public Post(
                final @NonNull  Player      player, 
                final @NonNull  Identifier  expression,
                final           int         level
            ) {
                super(player, expression, level);
            }
        }

        public boolean leveled() {
            return level > 0;
        }
    }

    @Getter
    public static sealed abstract class Perform extends PlayerExpressionEvent {

        private final int       level;

        private Perform(
                final @NonNull  Player      player, 
                final @NonNull  Identifier  expression,
                final           int         level
        ) {
            super(player, expression);
            this.level = level;
        }
        
        public static final class Pre extends Perform implements ICancellableEvent {
            public Pre(
                final @NonNull  Player      player, 
                final @NonNull  Identifier  expression,
                final           int         level
            ) {
                super(player, expression, level);
            }
        }
        
        public static final class Post extends Perform {
            public Post(
                final @NonNull  Player      player, 
                final @NonNull  Identifier  expression,
                final           int         level
            ) {
                super(player, expression, level);
            }
        }
    }
    
    public static final class Interrupt extends PlayerExpressionEvent {
        public Interrupt(
                final @NonNull  Player      player, 
                final @NonNull  Identifier  expression
        ) {
            super(player, expression);
        }
    }

    public static final class Finished extends PlayerExpressionEvent {
        public Finished(
                final @NonNull  Player      player, 
                final @NonNull  Identifier  expression
        ) {
            super(player, expression);
        }
    }

}
