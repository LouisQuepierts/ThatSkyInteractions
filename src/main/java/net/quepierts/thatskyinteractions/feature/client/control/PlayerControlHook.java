package net.quepierts.thatskyinteractions.feature.client.control;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerBone;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.client.control.event.LocalPlayerTurnEvent;
import net.quepierts.thatskyinteractions.feature.mixin.vanilla.client.accessor.ClientInputAccessor;

@UtilityClass
@EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID)
public class PlayerControlHook {

    @SubscribeEvent
    public static void onRestrictPlayerMotion(final MovementInputUpdateEvent event) {
        final var player        = event.getEntity();
        final var input         = event.getInput();
        final var data          = PlayerAnimationSystem.getAnimationData(player);
        final var animState     = data.getAnimation();

        if (!animState.isPlaying()) {
            return;
        }

        final var moveVector    = input.getMoveVector();
        final var moved         = moveVector.x != 0.0 || moveVector.y != 0.0;

        if (!moved) {
            return;
        }

        final var definition    = animState.getDefinition();
        final var fsmState      = animState.getFsmState();
        final var animation     = animState.getAnimation();

        if (definition.abortable() || animation.isLooping(fsmState)) {
            animation.exit(fsmState);
        }

        if (definition.restrictMotion()) {
            input.keyPresses    = Input.EMPTY;
            ((ClientInputAccessor) input).a4j$setMoveVector(Vec2.ZERO);
        }
    }

    @SubscribeEvent
    public static void onLocalPlayerTurn(final LocalPlayerTurnEvent event) {
        final var player        = event.getPlayer();
        final var xo            = event.getXo();

        final var data          = PlayerAnimationSystem.getAnimationData(player);
        final var animation     = data.getAnimation();

        if (!animation.isPlaying()) {
            return;
        }

        final var definition    = animation.getDefinition();
        final var minecraft     = Minecraft.getInstance();

        final var firstPerson   = minecraft.options.getCameraType().isFirstPerson();
        final var lock          = !definition.unlock().contains(PlayerBone.HEAD);

        if (!lock) {
            return;
        }

        if (firstPerson) {
            event.setCanceled(true);
            return;
        }

        final var deltaY        = (float) xo * 0.15f;
        final var headDiff0     = Mth.abs(Mth.wrapDegrees(player.getYRot() - player.yBodyRot));
        final var headDiff1     = Mth.abs(Mth.wrapDegrees(player.getYRot() - player.yBodyRot - deltaY));
        final var maxDiff       = player.isBlocking() ? 14f : 49f;

        if (headDiff0 > maxDiff && headDiff1 < headDiff0) {
            event.setXo(0.0);
        }
    }

}
