package net.quepierts.thatskyinteractions.feature.mixin.vanilla.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import net.quepierts.thatskyinteractions.feature.client.animation.PlayerAnimationHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @WrapOperation(
            method = "turnPlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"
            )
    )
    private void a4j$turnPlayer(
            final LocalPlayer instance,
            final double xo,
            final double yo,
            final Operation<Void> original
    ) {
        PlayerAnimationHook.onRestrictPlayerTurn(instance, xo, yo);
    }

}
