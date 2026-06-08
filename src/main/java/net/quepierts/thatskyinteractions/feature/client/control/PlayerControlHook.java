package net.quepierts.thatskyinteractions.feature.client.control;

import lombok.experimental.UtilityClass;
import net.minecraft.client.player.ClientInput;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.common.NeoForge;
import net.quepierts.thatskyinteractions.feature.client.control.event.LocalPlayerMovedEvent;
import net.quepierts.thatskyinteractions.feature.mixin.vanilla.client.accessor.ClientInputAccessor;

@UtilityClass
public class PlayerControlHook {

    public static void onUpdatePlayerMotion(
            final Player        player,
            final ClientInput   input
    ) {

        final var moveVector    = input.getMoveVector();
        final var moved         = moveVector.x != 0.0 || moveVector.y != 0.0;

        if (!moved) {
            return;
        }

        final var mEvent        = new LocalPlayerMovedEvent(player, input.keyPresses, moveVector);
        NeoForge.EVENT_BUS      .post(mEvent);

        if (mEvent.isCanceled()) {
            input.keyPresses    = Input.EMPTY;
            ((ClientInputAccessor) input).a4j$setMoveVector(Vec2.ZERO);
        }

        if (mEvent.isRedirected()) {
            input.keyPresses    = mEvent.getRedirectInput();
            ((ClientInputAccessor) input).a4j$setMoveVector(mEvent.getRedirectVector());
        }

    }

}
