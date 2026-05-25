package net.quepierts.thatskyinteractions.feature.mixin.vanilla.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.GameRenderer;
import net.neoforged.neoforge.common.NeoForge;
import net.quepierts.thatskyinteractions.feature.client.gui.layer.GameLayerHook;
import net.quepierts.thatskyinteractions.feature.client.render.GameRendererUpdateEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(
            method = "update",
            at = @At("TAIL")
    )
    private static void tsi$update(
            final DeltaTracker  deltaTracker,
            final boolean       advanceGameTime,
            final CallbackInfo  ci
    ) {
        NeoForge.EVENT_BUS.post(new GameRendererUpdateEvent());
    }

    @Inject(
            method = "extractGui",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Gui;extractSavingIndicator(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"
            )
    )
    private static void tsi$topLayer(
            final DeltaTracker deltaTracker,
            final boolean shouldRenderLevel,
            final boolean resourcesLoaded,
            final CallbackInfo ci,
            @Local(name = "graphics") GuiGraphicsExtractor graphics
    ) {
        GameLayerHook.onRenderLayer(graphics, deltaTracker);
    }

}
