package net.quepierts.thatskyinteractions.feature.mixin.vanilla.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.client.ClientHooks;
import net.quepierts.thatskyinteractions.feature.client.gui.screen.AnimatableScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientHooks.class)
public class ClientHookMixin {

    @Inject(
            method = "pushGuiLayer",
            at = @At(
                    value = "HEAD"
            )
    )
    private static void tsi$hide(
            final Minecraft     minecraft,
            final Screen        screen,
            final CallbackInfo  ci
    ) {
        if (minecraft.screen instanceof AnimatableScreen<?,?> animatable) {
            animatable.hide();
        }
    }

    @Inject(
            method = "popGuiLayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/client/ClientHooks;popGuiLayerInternal(Lnet/minecraft/client/Minecraft;)V",
                    shift = At.Shift.AFTER
            )
    )
    private static void tsi$show(
            final Minecraft     minecraft,
            final CallbackInfo  ci
    ) {
        if (minecraft.screen instanceof AnimatableScreen<?,?> animatable) {
            animatable.show();
        }
    }


}
