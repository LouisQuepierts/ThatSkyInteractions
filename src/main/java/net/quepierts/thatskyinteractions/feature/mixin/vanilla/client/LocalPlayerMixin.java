package net.quepierts.thatskyinteractions.feature.mixin.vanilla.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.quepierts.thatskyinteractions.feature.client.bond.ClientBondSystem;
import net.quepierts.thatskyinteractions.feature.client.control.PlayerControlHook;
import net.quepierts.thatskyinteractions.feature.registry.AttachmentTypes;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Shadow
    public ClientInput input;

    @Inject(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/tutorial/Tutorial;onInput(Lnet/minecraft/client/player/ClientInput;)V"
            )
    )
    private void a4j$onInput(CallbackInfo ci) {

        PlayerControlHook.onUpdatePlayerMotion((LocalPlayer) (Object) this, this.input);

    }

    @WrapOperation(
            method = "pick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/ProjectileUtil;getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;"
            )
    )
    private static @Nullable EntityHitResult tsi$filter(
            final Entity                        except,
            final Vec3                          from,
            final Vec3                          _to,
            final AABB                          box,
            final Predicate<Entity>             matching,
            final double                        maxValue,
            final Operation<EntityHitResult>    original
    ) {

        final var carrying = ClientBondSystem.getLocalAttachment().getCarry().isCarrying();
        final Predicate<Entity> predicate = carrying ?
                entity -> {
                    final var attachment = entity.getExistingDataOrNull(AttachmentTypes.PLAYER_BOUND);
                    if (attachment != null && attachment.getCarry().getCarrier() == except) {
                        return false;
                    }
                    return EntitySelector.CAN_BE_PICKED.test(entity);
                } :
                matching;

        return original.call(except, from, _to, box, predicate, maxValue);
    }

}
