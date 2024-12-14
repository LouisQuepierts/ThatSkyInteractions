package net.quepierts.thatskyinteractions.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.quepierts.thatskyinteractions.client.gui.layer.UiCombiner;
import net.quepierts.thatskyinteractions.client.reference.Shaders;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow public abstract MapRenderer getMapRenderer();

    @Inject(
            method = "resize",
            at = @At("RETURN")
    )
    public void tsi$resizeGameRenderer(int width, int height, CallbackInfo ci) {
        Shaders.resize(width, height);
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Gui;render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V",
                    shift = At.Shift.AFTER
            )
    )
    public void tsi$drawLayers(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci, @Local GuiGraphics guiGraphics) {
        UiCombiner.TOP.render(guiGraphics, deltaTracker);
    }
}
