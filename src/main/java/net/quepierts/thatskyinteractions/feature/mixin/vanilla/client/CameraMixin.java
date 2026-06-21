package net.quepierts.thatskyinteractions.feature.mixin.vanilla.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.client.animation.PlayerAnimationHook;
import net.quepierts.thatskyinteractions.feature.client.control.ClientCameraSystem;
import net.quepierts.thatskyinteractions.feature.client.control.event.ComputeCameraPositionEvent;
import net.quepierts.thatskyinteractions.feature.client.handhoding.ClientHandholdingHandler;
import org.joml.*;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow
    private @Nullable Entity entity;

    @Shadow
    protected abstract void setPosition(final double x, final double y, final double z);

    @Shadow
    private Vec3 position;

    @Shadow
    protected abstract void setRotation(final float yRot, final float xRot, final float roll);

    @Shadow
    private float xRot;

    @Shadow
    private float yRot;

    @Shadow
    private float roll;

    @Inject(
            method = "alignWithEntity",
            at = @At("TAIL")
    )
    private void a4j$animate(
            final float partialTicks,
            final CallbackInfo ci
    ) {
        if (!(this.entity instanceof Avatar avatar)) {
            return;
        }

        final var data          = PlayerAnimationSystem.getAnimationData(avatar);
        final var controller    = data.getController();

        final var position = new Vector3d(this.position.x(), this.position.y(), this.position.z());
        final var rotation = new Vector3f(this.xRot, this.yRot, this.roll);
        final var modified = PlayerAnimationHook.onSetupCameraAnimation(
                controller,
                (Camera) (Object) this,
                position,
                rotation,
                partialTicks
        );

        if (modified) {
            this.setPosition(position.x(), position.y(), position.z());
            this.setRotation(rotation.y(), rotation.x(), rotation.z());
        }
    }

    @Inject(
            method = "getMaxZoom",
            at = @At("RETURN")
    )
    private void a4j$getMaxZoom(
            final CallbackInfoReturnable<Double> cir
    ) {
        ClientCameraSystem.updateMaxZoom(cir.getReturnValueF());
    }

    @WrapOperation(
            method = "alignWithEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Camera;setPosition(DDD)V"
            )
    )
    private void a4j$setPosition(
            final Camera            instance,
            final double            x,
            final double            y,
            final double            z,
            final Operation<Void>   original
    ) {

        final var event = new ComputeCameraPositionEvent(this.entity, x, y, z);
        NeoForge.EVENT_BUS.post(event);
        original.call(instance, event.getX(), event.getY(), event.getZ());

    }

    @Inject(
            method = "alignWithEntity",
            at = @At("HEAD")
    )
    private void a4j$alignWithEntity(
            final float partialTicks,
            final CallbackInfo ci
    ) {
        if (this.entity != Minecraft.getInstance().player) {
            return;
        }
        ClientHandholdingHandler.onCameraAlign(partialTicks);
    }

}
