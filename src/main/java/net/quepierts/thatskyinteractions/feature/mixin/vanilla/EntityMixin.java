package net.quepierts.thatskyinteractions.feature.mixin.vanilla;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.NeoForge;
import net.quepierts.thatskyinteractions.feature.control.event.EntityTurnEvent;
import net.quepierts.veynir.core.misc.Generic;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements IAttachmentHolder {

    @Shadow
    private Vec3 position;

    @Shadow
    public abstract float getBbHeight();

    @Shadow
    public abstract void refreshDimensions();

    @Shadow
    private @Nullable Entity vehicle;

    @Inject(
            method = "turn",
            at = @At("HEAD"),
            cancellable = true
    )
    private void tsi$beforeEntityTurn(
            final double        xo,
            final double        yo,
            final CallbackInfo  ci
    ) {
        final var event = new EntityTurnEvent.Pre(Generic.cast(this), xo, yo);
        NeoForge.EVENT_BUS.post(event);

        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "turn",
            at = @At("TAIL")
    )
    private void tsi$afterEntityTurn(
            final double        xo,
            final double        yo,
            final CallbackInfo  ci
    ) {
        NeoForge.EVENT_BUS.post(new EntityTurnEvent.Post(Generic.cast(this), xo, yo));
    }

    @ModifyExpressionValue(
            method = "startRiding(Lnet/minecraft/world/entity/Entity;ZZ)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/EntityType;canSerialize()Z"
            )
    )
    private boolean tsi$onStartRiding(
            final boolean   original,
            final Entity    entityToRide,
            final boolean   force
    ) {
        return original || (entityToRide instanceof Avatar && force);
    }

    /*@Inject(
            method = "startRiding(Lnet/minecraft/world/entity/Entity;ZZ)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;addPassenger(Lnet/minecraft/world/entity/Entity;)V"
            )
    )
    private void tsi$refreshDimensions(
            final Entity                            entityToRide,
            final boolean                           force,
            final boolean                           sendEventAndTriggers,
            final CallbackInfoReturnable<Boolean>   cir
    ) {
        if ((Object) this instanceof Player) {
            this.refreshDimensions();
        }
    }

    @Inject(
            method      = "positionRider(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity$MoveFunction;)V",
            at          = @At("HEAD"),
            cancellable = true
    )
    private void tsi$onPositionRider(
            final Entity                passenger,
            final Entity.MoveFunction   moveFunction,
            final CallbackInfo          ci
    ) {

        final var attachment    = this.getExistingDataOrNull(AttachmentTypes.PLAYER_BOUND);

        if (attachment == null) {
            return;
        }

        final var relation      = attachment.getCarry();

        if (relation.getCarried() != passenger) {
            return;
        }

        final var position      = this.position;
        moveFunction.accept(passenger, position.x, position.y + this.getBbHeight(), position.z);

        ci.cancel();

    }*/

}
