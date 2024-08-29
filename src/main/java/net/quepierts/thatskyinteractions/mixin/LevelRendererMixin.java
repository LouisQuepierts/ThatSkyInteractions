package net.quepierts.thatskyinteractions.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.util.CameraHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Shadow
    @Final
    private RenderBuffers renderBuffers;
    @Redirect(
            method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;getTimeOfDay(F)F"
            )
    )
    private float tsi$redirectDayTime(ClientLevel instance, float partialTick) {
        float unmodified = instance.getTimeOfDay(partialTick);
        CameraHandler handler = ThatSkyInteractions.getInstance().getClient().getCameraHandler();
        return handler.recomputeDayTime(unmodified);
    }
}
