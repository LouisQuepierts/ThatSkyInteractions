package net.quepierts.thatskyinteractions.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.util.CameraHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FogRenderer.class)
public class FogRendererMixin {
    @Redirect(
            method = "setupColor",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;getTimeOfDay(F)F"
            )
    )
    private static float tsi$redirectDayTime(ClientLevel instance, float partialTick) {
        float unmodified = instance.getTimeOfDay(partialTick);
        CameraHandler handler = ThatSkyInteractions.getInstance().getClient().getCameraHandler();
        return handler.recomputeDayTime(unmodified);
    }
}
