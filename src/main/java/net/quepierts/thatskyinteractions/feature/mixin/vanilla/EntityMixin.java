package net.quepierts.thatskyinteractions.feature.mixin.vanilla;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.quepierts.thatskyinteractions.feature.bond.PlayerBondSystem;
import net.quepierts.thatskyinteractions.feature.control.event.EntityTurnEvent;
import net.quepierts.veynir.core.misc.Generic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {

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

    @Inject(
            method = "stopRiding",
            at = @At("HEAD")
    )
    private void tsi$onStopRiding(
            final CallbackInfo ci
    ) {

        if ((Object) this instanceof ServerPlayer self) {
            PlayerBondSystem.unRide(self);
        }

    }

}
