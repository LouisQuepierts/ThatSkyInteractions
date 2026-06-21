package net.quepierts.thatskyinteractions.feature.mixin.vanilla.client;

import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.quepierts.thatskyinteractions.feature.client.control.PlayerControlHook;
import net.quepierts.thatskyinteractions.feature.client.bond.ClientBondHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @Inject(
            method = "aiStep",
            at = @At("TAIL")
    )
    private void tsi$aiStep(CallbackInfo ci) {

        ClientBondHandler.afterAiStep((LocalPlayer) (Object) this);

    }

}
