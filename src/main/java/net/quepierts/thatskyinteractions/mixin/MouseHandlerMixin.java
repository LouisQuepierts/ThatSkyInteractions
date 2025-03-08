package net.quepierts.thatskyinteractions.mixin;

import net.minecraft.client.MouseHandler;
import net.quepierts.thatskyinteractions.client.ClientHelper;
import net.quepierts.thatskyinteractions.client.gui.layer.RouletteLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Shadow private double accumulatedDX;

    @Shadow private double accumulatedDY;

    @Inject(
            method = "turnPlayer",
            at = @At("HEAD"),
            cancellable = true
    )
    private void tsi$limitTurn(double movementTime, CallbackInfo ci) {
        if (ClientHelper.isRouletteOpen()) {
            RouletteLayer.INSTANCE.onMouseMove((float) this.accumulatedDX, (float) this.accumulatedDY);
            ci.cancel();
        }
    }
}
