package net.quepierts.thatskyinteractions.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.quepierts.thatskyinteractions.client.registry.PostEffects;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(
            method = "resize",
            at = @At("RETURN")
    )
    public void tsi$resizeGameRenderer(int width, int height, CallbackInfo ci) {
        PostEffects.resize(width, height);
    }
}
