package net.quepierts.thatskyinteractions.feature.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.quepierts.thatskyinteractions.feature.bond.PlayerBondAttachment;
import net.quepierts.thatskyinteractions.feature.bond.PlayerBondSystem;
import net.quepierts.thatskyinteractions.feature.utils.PlayerUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {



    @Inject(
            method = "dismountVehicle",
            at = @At("TAIL")
    )
    private void tsi$onStopRiding(
            final CallbackInfo ci
    ) {

        if ((Object) this instanceof ServerPlayer self) {
            PlayerBondSystem.unRide(self);
            self.refreshDimensions();
        }

    }

    @WrapOperation(
            method = "dismountVehicle",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;getDismountLocationForPassenger(Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/phys/Vec3;"
            )
    )
    private Vec3 tsi$getDismountLocation(
            final Entity            vehicle,
            final LivingEntity      passenger,
            final Operation<Vec3>   original
    ) {

        if (vehicle instanceof ServerPlayer player) {

            final var attachment = PlayerBondSystem.getAttachment(player);
            if (attachment.getCarry().getCarried() == (Object) this) {
                final var yRot = player.getYRot() + 90.0F;
                final float f = Mth.cos(yRot * Mth.DEG_TO_RAD);
                final float f1 = Mth.sin(yRot * Mth.DEG_TO_RAD);
                return player.position().add(f, 0, f1);
            }

        }

        return original.call(vehicle, passenger);
    }

}
