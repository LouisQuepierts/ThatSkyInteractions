package net.quepierts.thatskyinteractions.mixin;

import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.util.CameraHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class CameraMixin {
    @Shadow private Vec3 position;
    @Shadow @Final private BlockPos.MutableBlockPos blockPosition;
    @Inject(
            method = "setPosition(Lnet/minecraft/world/phys/Vec3;)V",
            at = @At("RETURN")
    )
    public void tsi$recomputePosition(Vec3 pPos, CallbackInfo ci) {
        CameraHandler handler = ThatSkyInteractions.getInstance().getClient().getCameraHandler();
        Vec3 recomputed = handler.recomputeCameraPosition(pPos);
        this.position = recomputed;
        this.blockPosition.set(recomputed.x, recomputed.y, recomputed.z);
    }
}
