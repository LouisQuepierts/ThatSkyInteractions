package net.quepierts.thatskyinteractions.feature.expression;

import it.unimi.dsi.fastutil.PriorityQueue;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.animation.event.PlayerAnimationControllerEvent;

@Slf4j
@UtilityClass
@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public class PlayerExpressionHandler {

    // maybe it is unnecessary?
    /*@SubscribeEvent
    public static void onPlayerLoggedOut(final PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerExpressionSystem.cancel(player);
        }
    }*/

    @SubscribeEvent
    public static void onPlayerTick(final PlayerTickEvent.Pre event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        final var attachment    = PlayerExpressionSystem.getAttachment(player);
        final var expression    = attachment.getReference().get();

        if (expression == null) {

            final var pending   = attachment.dequeue();

            if (pending == null) {
                return;
            }

            if (pending.leveled()) {
                PlayerExpressionSystem.perform(player, pending.identifier(), pending.level());
            } else {
                PlayerExpressionSystem.perform(player, pending.identifier());
            }

            return;
        }

        final var finished      = expression.isFinished(player, attachment.getState());
        if (finished) {
            PlayerExpressionSystem.finish(player);
        }
    }

    @SubscribeEvent
    public static void onAnimationFinished(final PlayerAnimationControllerEvent.Finished event) {

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        final var attachment    = PlayerExpressionSystem.getAttachment(player);
        final var expression    = attachment.getReference().get();

        if (expression == null) {
            return;
        }

        expression.onAnimationFinished(
                player,
                attachment.getState(),
                event
        );

    }

    @SubscribeEvent
    public static void onAnimationTransitionStart(final PlayerAnimationControllerEvent.State.TransitionStart event) {

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        final var attachment    = PlayerExpressionSystem.getAttachment(player);
        final var expression    = attachment.getReference().get();

        if (expression == null) {
            return;
        }

        expression.onAnimationTransitionStart(
                player,
                attachment.getState(),
                event
        );

    }

    @SubscribeEvent
    public static void onAnimationTransitionEnd(final PlayerAnimationControllerEvent.State.TransitionEnd event) {

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        final var attachment    = PlayerExpressionSystem.getAttachment(player);
        final var expression    = attachment.getReference().get();

        if (expression == null) {
            return;
        }

        expression.onAnimationTransitionEnd(
                player,
                attachment.getState(),
                event
        );

    }

    @SubscribeEvent
    public static void onAnimationLooped(final PlayerAnimationControllerEvent.State.Loop event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        final var attachment    = PlayerExpressionSystem.getAttachment(player);
        final var expression    = attachment.getReference().get();

        if (expression == null) {
            return;
        }

        expression.onAnimationLooped(
                player,
                attachment.getState(),
                event
        );
    }
}
