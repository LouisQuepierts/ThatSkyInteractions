package net.quepierts.thatskyinteractions.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.CameraHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Redirect(
            method = {
                    "getSkyDarken",
                    "getSkyColor",
                    "getCloudColor",
                    "getStarBrightness"
            },
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
